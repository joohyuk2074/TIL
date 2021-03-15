# 스토리지 드라이버

Linux 시스템에서 docker는 이미지, 볼륨 등과 관련된 데이터를 /var/lib/docker에 저장합니다.

<br>

docker build 명령어를 실행하면 docker는 dockerfile의 각 명령에 대해 하나의 레이어를 빌드합니다. 이러한 이미지 레이어는 읽기 전용 레이어 입니다. docker run 명령을 실행하면 docker는 읽기-쓰기 레이어인 컨테이너 레이어를 빌드합니다.

## OverlayFS 스토리지 드라이버 사용

OverlayFS는 AUFS와 유사하지만 더 빠르고 간단한 구현을 가진 최신 통합 파일시스템 입니다. OverlayFS는 AUFS와 비슷한 원리로 동작하지만 좀 더 간단한 구조로 사용되며 성능 또한 좀 더 좋기 때문에 최신버전의 도커는 OverlayFS를 기본적으로 사용하고 있습니다.
<br><br>
OverlayFS는 overlay와 더 새롭고 안정적인 overlay2로 나뉩니다. OverlayFS를 사용하는경우 overlay대신 overlay2 스토리지 드라이버를 사용하는것이 권장됩니다.

## overlay 작동방식
OverlayFS는 단일 Linux 호스트에 두 개의 디렉토리를 계층화 하고 단일 디렉토리로 제공합니다. 이러한 디렉토리를 레이어라고 하며 통합 프로세스를 유니온 마운트(union mount)라고 합니다. OverLayFS는 하위 디렉토리를 lowerdir로, 상위 디렉토리를 upperdir로 참조 합니다. 통합된 뷰는 merged라는 자체 디렉토리를 통해 노출됩니다.

<br>

이미지 레이어는 lowerdir이고 컨테이너 레이어는 upperdir 입니다. 통합 된 뷰는 컨테이너 마운트 인 merged라는 디렉토리를 통해 노출 됩니다.
![overlay구조](https://docs.docker.com/storage/storagedriver/images/overlay_constructs.jpg)


overlay 드라이버는 두 개의 레이어에서만 작동합니다. 즉, 다중 OverlayFS 레이어로 다중 레이어 이미지를 구현할 수 없음을 뜻합니다. 대신에 각 이미지 레이어는 /var/lib/docker/overlay 아래에 자체 디렉토리로 구현됩니다. 그런 다음 하드 링크는 하위 계층과 공유되는 데이터를 참조하는 방식으로 구성됩니다. 하드 링크를 사용하면 기존 오버레이 스토리지 드라이버의 알려진 제한 사항인 inode가 과도하게 사용되며 백업 파일 시스템의 추가 구성이 필요할 수 있습니다.


컨테이너를 만들기 위해 overlay 드라이버는 이미지의 최상위 레이어를 나타내는 디렉토리와 컨테이너의 새 디렉토리를 결합합니다. 이미지의 최상위 레이어는 lowerdir이며 읽기 전용입니다. 컨테이너의 새 디렉토리는 upperdir이면 쓰기 가능합니다.

## 디스크상의 이미지 및 컨테이너 레이어
다음 docker pull 명령은 5개의 레이어로 구성된 Docker 이미지를 다운로드하는 Docker 호스트를 보여줍니다.


## Docker 스토리지로 overlay 설정 
overlay 스토리지 드라이버를 사용하도록 Docker를 구성하려면 Docker 호스트가 Linux 커널 3.18 버전 이상을 실행해야 합니다. overlay2 드라이버의 경우는 4.0 버전 이상이어야 합니다.

<br>

overlay2 스토리지 드라이버를 설정해 보겠습니다.

설정해 보기 전에 먼저 커널 버전과 overlay 커널 모듈이 사용가능한 상태인지를 확인합니다.

<br>

커널 버전 확인
```
# uname -r
5.4.0-1038-aws
```

overlay 커널 모듈이 사용 가능한 상태인지 확인
```
# grep overlay /proc/filesystems
nodev overlay
```

<br>

1. 도커를 중지합니다.
    ```
    # sudo systemctl stop docker
    ```

2. /var/lib/docker의 내용을 임시위치에 복사합니다.
    ```
    # cp -au /var/lib/docker /var/lib/docker.bk
    ```

3. /etc/docker/daemon.json을 편집합니다. 존재하지 않는 경우 파일을 생성하여 작성합니다.
    ```json
    {
        "storage-driver": "overlay2"
    }
    ```
    > 주의해야 할 점은 daemon.json 파일에 잘못된 형식의 JSON이 포함 된 경우 Docker가 시작되지 않습니다.

4. Docker를 실행시킵니다.
    ```
    # sudo systemctl start docker
    ```

5. 도커 데몬이 overlay2 스토리지 드라이버를 사용하고 있는지 확인하고 `docker info` 명령어를 사용해 스토리지 드라이버 및 백업 파일 시스템을 찾습니다.
    ```
    # docker info
    Containers: 0
    Images: 0
    Storage Driver: overlay2
    Backing Filesystem: xfs
    Supports d_type: true
    Native Overlay Diff: true
    <...>
    ```
    Docker는 이제 overlay2 스토리지 드라이버를 사용하고 있는걸 확인할 수 있습니다.
# 도커 데몬

## 1. 도커의 구조
Docker가 실제로 어디있는지 알아보겠습니다.
```
# which docker
/usr/bin/docker
```
docker 명령어는 /usr/bin/docker에 위치한 파일을 통해 사용되고 있습니다. <br>

이번에는 실행 중인 도커 프로세스를 확인 해보겠습니다. 
```
# ps aux | grep docker
root     17594  0.0  0.1  14860  1064 pts/0    S+   09:19   0:00 grep --color=auto docker
root     21906  0.0  7.7 911984 77864 ?        Ssl  Mar07   0:54 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
```
확인해보니 /usr/bin/docker가 실행중일줄 알았는데 /usr/bin/dockerd가 실행중입니다. 뭔가 이상합니다. 도커 구조를 살펴보겠습니다.

![도커의 구조](https://docs.docker.com/engine/images/architecture.svg)


### Docker daemon
Docker 데몬(dockerd)은 API 요청을 수신하고 이미지, 컨테이너, 네트워크 및 볼륨과 같은 Docker 객체를 관리합니다.

### Docker client
Docker 클라이언트는 많은 Docker 사용자가 Docker와 상호작용하는 기본방법 입니다. `docker run`과 같은 명령을 사용하면 클라이언트가 이러한 명령을 API로서 dockerd로 보내 실행합니다. 이때 Docker 클라이언트는 /var/run/docker.sock에 위치한 유닉스 소켓을 통해 도커 데몬의 API를 호출합니다. 도커 클라이언트가 사용하는 유닉스 소켓은 같은 호스트 내에 있는 도커 데몬에게 명령을 전달할 때 사용됩니다. tcp로 원격에 있는 도커 데몬을 제어하는 방법도 있습니다.

### Docker registries
Docker registries는 Docker 이미지를 저장합니다. Docker Hub는 누구나 사용할 수 있는 공용 레지스트리이며 Docker는 기본적으로 Docker Hub에서 이미지를 찾습니다.

<br>
Docker는 클라이언트-서버 구조를 가집니다. 
Docker 클라이언트는 Docker 컨테이너를 빌드, 실행 및 배포에 대한 무거운 작업을 수행하는 Docker 데몬과 통신을 합니다. Docker 클라이언트와 데몬은 동일한 시스템에서 실행되거나 Docker 클라이언트를 원격 Docker 데몬에 연결할 수 있습니다. 이런 Docker 클라이언트와 데몬은 UNIX 소켓 또는 네트워크 인터페이스를 통해 REST API를 사용하여 통신합니다. 
<br><br>
결국은 컨테이너나 이미지를 다루는 명령어는 /usr/bin/docker에서 실행되지만 도커 엔진의 프로세스는 /usr/bin/dockerd 파일로 실행되고 있습니다. 이는 docker 명령어가 실제 도커 엔진이 아닌 클라이언트로서의 도커라는 것을 알 수 있습니다.
<hr>


## 2. 도커 데몬 실행
```
# service docker stop
# dockerd
...
used to set a preferred IP address
INFO[2021-03-13T09:30:42.018349427Z] Loading containers: done.
INFO[2021-03-13T09:30:42.055862531Z] Docker daemon                                 commit=363e9a8 graphdriver(s)=overlay2 version=20.10.4
INFO[2021-03-13T09:30:42.056762465Z] Daemon has completed initialization
INFO[2021-03-13T09:30:42.086503239Z] API listen on /var/run/docker.sock
```

도커 데몬을 실행시키면 마지막에 /var/run/docker.sock에서 입력(listen)을 받을 수 있는 상태라는 메시지가 출력됩니다.

<hr>

## 3. 도커 데몬 모니터링
### 3.1 도커 데몬 디버그 모드
도커 데몬에서 어떤 일이 일어나고 있는지 가장 확실하고 정확하게, 그리고 자세히 알아내는 방법은 도커 데몬을 디버그 옵션으로 실행하는 것입니다. 이렇게 하면 Remote API의 입출력뿐만 아니라 로컬 도커 클라이언트에서 오가는 모든 명령어를 로그로 출력합니다. 디버그 모드는 도커 데몬을 실행할 때 -D 옵션을 추가해서 사용할 수 있습니다.

```
# dockerd -D
INFO[2021-03-16T16:46:51.072759627Z] Starting up
DEBU[2021-03-16T16:46:51.073371657Z] Listener created for HTTP on unix (/var/run/docker.sock)
INFO[2021-03-16T16:46:51.073633924Z] detected 127.0.0.53 nameserver, assuming systemd-resolved, so using resolv.conf: /run/systemd/resolve/resolv.conf
DEBU[2021-03-16T16:46:51.073998912Z] Golang's threads limit set to 6840
INFO[2021-03-16T16:46:51.074444262Z] parsed scheme: "unix"                         module=grpc
```

그런데 원치 않는 로그까지 너무 많이 출력되며, 호스트에 있는 파일을 읽거나 도커 데몬을 포그라운드 상태로 실행해야 한다는 단점이 있어 도커가 제공하는 명령어를 통해 도커 데몬을 모니터링 해보겠습니다.

### 3.2 events, stats, system df 명령어

#### 3.2.1 events
`events`명령어는 도커가 기본으로 제공하는 명령어 입니다. `events`명령어는 도커 데몬에 어떤 일이 일어나고 있는지를 실시간 스트림 로그로 보여줍니다. 
```
# docker events
```
위 명령어를 입력해도 어떠한 이벤트도 도커 데몬에 발생하지 않아서 아무것도 출력되지 않습니다. 새로운 터미널을 연 뒤에 ubuntu 이미지를 pull 해보겠습니다.

<br>
이미지의 pull이 완료되면 docker events를 실행했던 터미널에서 다음과 같은 명령어가 출력되는 것을 확인할 수 있습니다.

```
# docker events
2021-03-16T16:58:01.862463109Z image pull ubuntu:latest (name=ubuntu)
```

특정  항목에 대한 출력 결과만 보고 싶다면 --filter 옵션을 설정하면 됩니다. 출력의 종류는 container, image, volume, network, plugin, daemon이 있습니다.
```
docker events --filter 'type=image'
``` 
type외에도 [공식문서](https://docs.docker.com/engine/reference/commandline/events/)를 참조하면 다른 조건들에 대한 출력 결과를 볼 수 있습니다. 

#### 3.2.2 stats
`stats`  명령어는 실행 중인 모든 컨테이너의 자원 사용량을 스트림으로 출력합니다.
```
# docker stats
```
`stats` 명령어는 실행 중인 모든 컨테이너의 CPU, 메모리 제한 및 사용량, 네트워크 입출력(I/O), 블록 입출력(하드웨어 입출력) 정보를 출력합니다.


#### 3.2.3 system df
`system df`명령어는 도커에서 사용하고 있는 이미지, 컨테이너, 로컬 볼륨의 총 개수 및 사용 중인 개수, 크기, 삭제함으로써 확보 가능한 공간을 출력합니다. 

```
# docker system df
TYPE                TOTAL               ACTIVE              SIZE                RECLAIMABLE
Images              1                   1                   72.9MB              0B (0%)
Containers          2                   0                   8B                  8B (100%)
Local Volumes       0                   0                   0B                  0B
Build Cache         0
```
RECLAMIMABLE 항목은 사용 중이지 않은 이미지를 삭제함으로써 확보할 수 있는 공간을 의미합니다.

<hr>

## 4. 스토리지 드라이버
도커는 특정 스토리지 백엔드 기술을 사용해 도커 컨테이너와 이미지를 저장하고 관리합니다. 일부 운영체제는 도커를 설치할 때 기본적으로 사용하도록 설정된 스토리지 드라이버가 있는데 우분투 같은 데비안 계열 운영체제는 overlay2를 사용합니다. 

`docker info`명령어로 확인해 보겠습니다.
```
# docker info | grep "Storage Driver"
Storage Driver: overlay2
```

도커 데몬 실행 옵션에서 스토리지 드라이버를 변경할 수도 있습니다.
```
# dockerd --storage-driver=devicemapper
```

### 4.1 스토리지 드라이버의 원리
스토리지 드라이버를 사용하면 컨테이너의 쓰기 가능 계층에 데이터를 생성 할 수 있습니다. 우선 스토리지 드라이버를 알아보기 전에 도커의 이미지와 레이어가 어떻게 구성되는지 살펴 보겠습니다.

Docker 이미지는 일련의 레이어로 구성됩니다. 각 레이어는 이미지의 Dockerfile에 있는 명령어를 나타냅니다. 다음 Dockerfile을 살펴 보겠습니다.
```Dockerfile
FROM ubuntu:18.04
COPY . /app
RUN make /app
CMD python /app/app.py
```
이 Dockerfile에는 각각 계층을 생성하는 네 개의 명령이 포함되어 있습니다. 


새 컨테이너를 만들 때 기본레이어 위에 새 쓰기 가능한 레이어를 추가하고 이 레이어를 보통 `container layer` 라고 합니다. 새 파일 쓰기, 기존 파일 수정 및 파일 삭제와 같이 실행중인 컨테이너에 대한 모든 변경 사항은 `container layer`에 기록됩니다.

![레이어](https://docs.docker.com/storage/storagedriver/images/container-layers.jpg)
스토리지 드라이버는 이러한 계층이 서로 상호 작용하는 방식에 대한 세부 정보를 처리합니다. 상황에 따라 장점과 단점이 있는 다양한 스토리지 드라이버를 사용할 수 있습니다.

<br>
실제로 컨테이너 내부에서 읽기와 새로운 파일 쓰기, 기존의 파일 쓰기 작업이 일어날 때는 드라이버에 따라 Copy-on-Write(Cow)와 같은 개념을 사용합니다. 그래서 이 개념에 대해 간단히 짚고 넘어가겠습니다.

<br>

#### 4.1.1 Copy-on-Write(CoW)
Copy-on-Write는 최대의 효율성을 위해 파일을 공유하고 복사하는 전략입니다. 이미지 내의 하위 레이어에 파일이나 디렉토리가 존재하고 최상위 레이어(쓰기 가능 계층)에 읽기 액세스가 필요한 경우 기존파일만 사용합니다. 반면 다른 레이어가 파일을 처음 수정해야 할 때 파일이 해당 레이어에 복사되고 수정됩니다.

![CoW](https://www.oreilly.com/library/view/getting-started-with/9781838645700/assets/dfc9cf05-7ad2-4f58-87a4-4702cd72dbbc.jpg)

### 4.2 overlayFS
OverlayFS는 다른 스토리지 드라이버와는 달리 계층화된 이미지 구조를 사용하지 않으며, lowedir이라는 단일화된 이미지 레이어를 사용합니다. 하위 디렉토리를 lowerdir로, 상위 디렉토리를 upperdir로 참조합니다. 통합 뷰는 merged라는 자체 디렉토리를 통해 노출됩니다.

![overlay구조](https://docs.docker.com/storage/storagedriver/images/overlay_constructs.jpg)


ubuntu 이미지를 받아와서 컨테이너를 생성해 변경사항을 만들어 보겠습니다.

ubuntu 이미지 가져오기
```
# docker pull ubuntu:14.04
14.04: Pulling from library/ubuntu
2e6e20c8e2e6: Pull complete
95201152d9ff: Pull complete
5f63a3b65493: Pull complete
Digest: sha256:63fce984528cec8714c365919882f8fb64c8a3edf23fdfa0b218a2756125456f
Status: Downloaded newer image for ubuntu:14.04
docker.io/library/ubuntu:14.04
```

컨테이너 실행
```
# docker run -i -t --name container ubuntu:14.04
root@12e303088d84:/# echo my file! >> overlayfile
```

호스트로 빠져나와 /var/lib/docker/overlay2 디렉토리의 내용을 보면 다음과 같이 컨테이너와 이미지의 파일을 담고 있는 디렉토리가 존재하는 것을 확인할 수 있습니다.

ubuntu 이미지를 다운로드 한 후 /var/lib/docker/overlay2 아래에 4개의 디렉토리를 볼 수 있습니다.
```
# ls -l /var/lib/docker/overlay2
total 16
drwx------ 4 root root 4096 Mar 16 16:31 73752f2027e257307c502753955b8307b625e195cd27a1c08ef6612f484746af
drwx------ 3 root root 4096 Mar 16 16:31 981c5c5acdf4169f7ce87c72a5db5f59a23befaf15791fcb8ef532bc03adabca
drwx------ 4 root root 4096 Mar 16 16:31 c783d26bc409b21f61f9080b2b653289cb5212f7ff0cdd37e938bb0688fa6d68
drwx------ 2 root root 4096 Mar 16 16:31 l
```

l 디렉토리에는 단축된 레이어 식별자가 심볼릭 링크로 포함되어 있습니다.
```
# ls -l /var/lib/docker/overlay2/l
total 12
lrwxrwxrwx 1 root root 72 Mar 16 16:31 3COF6GXVV7XOR7XNVUGY6EUVUI -> ../73752f2027e257307c502753955b8307b625e195cd27a1c08ef6612f484746af/diff
lrwxrwxrwx 1 root root 72 Mar 16 16:31 GXK3BQEBN77IJCXKGHWRLHGAZI -> ../c783d26bc409b21f61f9080b2b653289cb5212f7ff0cdd37e938bb0688fa6d68/diff
lrwxrwxrwx 1 root root 72 Mar 16 16:31 NAO6MYAM2VGYAJTMSECSHJJ2TB -> ../981c5c5acdf4169f7ce87c72a5db5f59a23befaf15791fcb8ef532bc03adabca/diff
```


가장 낮은 레이어에는 단축된 식별자의 이름이 포함된 link라는 파일과 레이어의 내용이 포함된 diff라는 디렉토리가 있습니다.
```
# ls /var/lib/docker/overlay2/981c5c5acdf4169f7ce87c72a5db5f59a23befaf15791fcb8ef532bc03adabca
committed  diff  link
```


참고자료 : <https://docs.docker.com/get-started/overview/><br>
참고자료 : <https://www.oreilly.com/library/view/getting-started-with/9781838645700/4da0f0db-5661-4599-91f7-53fc1ec62698.xhtml>

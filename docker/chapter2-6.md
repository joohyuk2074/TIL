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

### 디스크상의 이미지 및 컨테이너 레이어
다음 docker pull 명령은 3개의 레이어로 구성된 Docker 이미지를 다운로드하는 Docker 호스트를 보여줍니다.

```
Using default tag: latest
latest: Pulling from library/ubuntu
5d3b2c2d21bb: Pull complete
3fc2062ea667: Pull complete
75adf526d75b: Pull complete
Digest: sha256:b4f9e18267eb98998f6130342baacaeb9553f136142d40959a1b46d6401f0f2b
Status: Downloaded newer image for ubuntu:latest
docker.io/library/ubuntu:latest
```

### 이미지 레이어 
각 이미지 레이어에는 /var/lib/docker/overlay/ 내에 자체 디렉토리가 있고, 여기에는 아래와 같이 내용이 포함됩니다. 이미지 ID가 디렉토리 ID와 일치하지 않습니다.

## overlay2 작동 방식
OverlayFS는 단일 Linux 호스트에 두 개의 디렉토리를 계층화 하고 단일 디렉토리로 제공합니다. 이러한 디렉토리를 레이어라고 하며 통합 프로세스를 유니온 마운트(union mount)라고 합니다. OverLayFS는 하위 디렉토리를 lowerdir로, 상위 디렉토리를 upperdir로 참조 합니다. 통합된 뷰는 merged라는 자체 디렉토리를 통해 노출됩니다.

<br>

overlay2 드라이버는 기본적으로 최대 128개의 하위 OverlayFS 레이어를 지원합니다. 이 기능은 `docker build`및 `docker commit`과 같은 레이어 관련 Docker 명령에 대해 더 나은 성능을 제공하고 백업파일 시스템에서 더 작은 inode를 사용합니다.

### 디스크상의 이미지 및 컨테이너 레이어
`docker pull ubuntu`를 사용하여 3 계층 이미지를 다운로드 한 후 /var/lib/docker/overlay2 아래에 4개의 디렉토리를 볼 수 있습니다.

```
ubuntu@ip-172-31-39-57:~$ sudo docker pull ubuntu
Using default tag: latest
latest: Pulling from library/ubuntu
5d3b2c2d21bb: Pull complete
3fc2062ea667: Pull complete
75adf526d75b: Pull complete
Digest: sha256:b4f9e18267eb98998f6130342baacaeb9553f136142d40959a1b46d6401f0f2b
Status: Downloaded newer image for ubuntu:latest
docker.io/library/ubuntu:latest
```

```
ubuntu@ip-172-31-39-57:~$ sudo ls -l /var/lib/docker/overlay2
total 16
drwx-----x 4 root root 4096 Mar 15 05:27 2be35debadfc8e6d3b82301f55601c041381bef80d0a0ba39a11490d780b2de0
drwx-----x 4 root root 4096 Mar 15 05:27 cb4152c72f5f75bb189dce7b4bb9c86dedbc295abc19d2d7f8300c8b3f67709b
drwx-----x 3 root root 4096 Mar 15 05:27 e9fc64ad7a554643c6b10d798246e73ea86f1e91d9cef995dc99dfd30129dc04
drwx-----x 2 root root 4096 Mar 15 05:27 l
```
새로운 l 디렉토리에는 단축 된 레이어 식별자가 심볼릭 링크로 포함되어 있습니다. 이러한 식별자는 mount 명령에 대한 인수의 페이지 크기 제한에 도달하지 않도록하는데 사용됩니다. 

```
ubuntu@ip-172-31-39-57:~$ sudo ls -l /var/lib/docker/overlay2/l
total 12
lrwxrwxrwx 1 root root 72 Mar 15 05:27 D362HJQXATY4YYQN4TZWPV3XH5 -> ../cb4152c72f5f75bb189dce7b4bb9c86dedbc295abc19d2d7f8300c8b3f67709b/diff
lrwxrwxrwx 1 root root 72 Mar 15 05:26 NUNJAFIFOVK22B3YWH34O4YGWT -> ../e9fc64ad7a554643c6b10d798246e73ea86f1e91d9cef995dc99dfd30129dc04/diff
lrwxrwxrwx 1 root root 72 Mar 15 05:27 UX2YXIZCYH65BRUMTVBKJA5MZC -> ../2be35debadfc8e6d3b82301f55601c041381bef80d0a0ba39a11490d780b2de0/diff
```
가장 낮은 레이어에는 단축된 식별자의 이름이 포함된 link라는 파일과 레이어의 내용이 포함된 diff라는 디렉토리가 있습니다.

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







    ## 도커 데몬 설정 
### 도커 데몬 제어: -H
-H 옵션은 도커 데몬의 API를 사용할 수 있는 방법을 추가합니다. 아무런 옵션을 설정하지 않고 도커데몬을 실행하면 이전에 보았듯이 /usr/bin/docker를 위한 유닉스 소켓인 /var/run/docker.sock을 사용합니다. 즉, 다음의 두 명령어는 차이가 없습니다.

```
# dockerd
# dockerd -H unix:///var/run/docker.sock
```

Docker는 데몬과 클라이언트 간의 통신을 할 때 로컬에서는 유닉스 소켓을 사용하고, 원격에서는 TCP 소켓을 사용합니다. 여기에 HTTP REST 형식으로 API가 구현되어 있습니다.

그렇게 때문에 -H에 IP 주소와 포트번호를 입력하면 원격 API인 Docker Remote API로 도커를 제어할 수 있습니다. 
즉, 도커 클라이언트와는 다르게 로컬에 있는 도커 데몬이 아니더라도 제어할 수 있고 RESTful API 형식을 띠고 있으므로 HTTP 요청으로 도커를 제어할 수 있습니다.

다음과 같이 도커 데몬을 실행하면 호스트에 존재하는 모든 네트워크 인터페이스의 IP 주소와 2375번 포트를 바인딩해 입력을 받습니다.
```
# dockerd -H tcp://0.0.0.0:2375
```

기존의 Docker 데몬을 정지하고 TCP 소켓으로 다시 실행시켜 API를 테스트해보겠습니다.
```
sudo service docker stop
sudo docker -d -H tcp://0.0.0.0:4243
```
### 도커 데몬에 보안 적용: -tlsverify
보안 설정 없이 원격으로 docker daemon에 명령을 보내려고 했는데 
```
INFO[2021-03-14T14:02:01.430850526Z] Starting up
WARN[2021-03-14T14:02:01.431332361Z] Binding to IP address without --tlsverify is insecure and gives root access on this machine to everyone who has access to your network.  host="tcp://54.180.122.240:2375"
WARN[2021-03-14T14:02:01.431426929Z] Binding to an IP address, even on localhost, can also give access to scripts run in a browser. Be safe out there!  host="tcp://54.180.122.240:2375"
WARN[2021-03-14T14:02:02.431778298Z] Binding to an IP address without --tlsverify is deprecated. Startup is intentionally being slowed down to show this message  host="tcp://54.180.122.240:2375"
WARN[2021-03-14T14:02:02.431819389Z] Please consider generating tls certificates with client validation to prevent exposing unauthenticated root access to your network  host="tcp://54.180.122.240:2375"
WARN[2021-03-14T14:02:02.431831780Z] You can override this by explicitly specifying '--tls=false' or '--tlsverify=false'  host="tcp://54.180.122.240:2375"
WARN[2021-03-14T14:02:02.431840815Z] Support for listening on TCP without authentication or explicit intent to run without authentication will be removed in the next release  host="tcp://54.180.122.240:2375"
failed to load listeners: listen tcp 54.180.122.240:2375: bind: cannot assign requested address
```
이런 메시지를 띄우면서 dockerd가 실행되지 않았습니다. 확인해보니 --tlsverify없이 IP 주소에 바인딩하는 것은 더이상 사용되지 않는다고 합니다. 따라서 보안 설정을 먼저 한 후에 원격으로 명령을 보내보겠습니다.

보안이 적용돼 있지 않으면 Remote API를 위해 바인딩된 IP 주소와 포트 번호만 알면 도커를 제어할 수 있기 때문에 그것을 방지하기 위함으로 보입니다.

따라서 도커 데몬에 TLS 보안을 적용하고, 도커 클라이언트와 Remote API 클라이언트가 인증되지 않으면 도커 데몬을 제어할 수 없도록 설정해 보겠습니다.

<br>

![도커 데몬에 보안을 적용할 때 사용되는 파일](https://postfiles.pstatic.net/20160622_211/alice_k106_14665947053509lgYO_PNG/%B1%D7%B8%B22.png?type=w2)

<br>

#### 1. 서버측 파일 생성
1. 인증서에 사용될 키를 생성합니다.

    ```
    # mkdir keys && cd keys
    # openssl genrsa -aes256 -out ca-key.pem 4096
    ```

2. 공용 키(public key)를 생성합니다.
   ```
   # openssl req -new -x509 -days 10000 -key ca-key.pem -sha256 -out ca.pem
   ```

3. 서버 측에서 사용될 키를 생성합니다.
    ```
    # openssl genrsa -out server-key.pem 4096
    ```

4. 서버 측에서 사용될 인증서를 위한 인증 요청서 파일을 생성합니다.
   ```
   # openssl req -subj "/CN=$HOST" -sha256 -new -key server-key.pem -out server.csr
   ```

5. 접속에 사용될 IP 주소를 extfile.cnf 파일로 저장합니다.
    ```
    # echo subjectAltName = IP:$HOST, IP:127.0.0.1 > extfile.cnf
    ```

6. 다음 명령을 입력해 서버 측의 인증서 파일을 생성합니다. 

    ```
    # opensssl x509 -req -days 365 -sha256 -in server.csr -CA ca.pem -CAkey ca-key.pem -CAcreateserial -out server-cert.pem -extfile extfile.cnf
    ```

#### 2. 클라이언트 측에서 사용할 파일 생성

1. 클라이언트 측의 키 파일과 인증 요청 파일을 생성하고, extfile.cnf 파일에 extendedKeyUsage 항목을 추가합니다.
    ```
    # openssl genrsa -out key.pem 4096
    # openssl req -subj '/CN=client' -new -key key.pem -out client.csr
    # echo extendedKeyUsage = clientAuth > extfile.cnf
    ```
2. 다음 명령을 입력해 클라이언트 측의 인증서를 생성합니다.
    ```
    # openssl x509 -req -days 30000 -sha256 -in client.csr -CA ca.pem -CAkey ca-key.pem -CAcreateserial -out cert.pem -extfile extfile.cnf
    ```
3. 생성된 파일의 쓰기 권한을 삭제해 읽기 전용 파일로 만듭니다.
    ```
    # chmod -v 0400 ca-key.pem key.pem server-key.pem ca.pem server-cert.pem cert.pem
    ```

4. 도커 데몬의 설정 파일이 존재하는 디렉터리인 ~/.docker로 도커 데몬 측에서 필요한 파일을 옮깁니다. 
    ```
    # cp {ca, server-cert, server-key, cert, key}.pem ~/.docker
    ```

보안 적용 파일을 모두 생성하였습니다. 이제 암호화가 적용된 도커 데몬을 실행합니다.
    ```
    # dockerd --tlsverify \
    --tlscacert=/home/ubuntu/.docker/ca.pem \
    --tlscert=/home/ubuntu/.docker/server-cert.pem \
    --tlskey=/home/ubuntu/.docker/server-key.pem \
    -H=0.0.0.0:2376 \
    -H unix:///var/run/docker.sock
    ```


```
# docker -H 54.180.122.240:2376 \
--tlscacert=/home/ubuntu/.docker/ca.pem \
--tlscert=/home/ubuntu/.docker/cert.pem \
--tlskey=/home/ubuntu/.docker/key.pem \
--tlsverify version
```
# 도커 데몬

## 1. 도커 데몬이란?
dockerd는 컨테이너를 관리하는 영구 프로세스입니다.

<hr>

## 2. 도커의 구조
Docker는 클라이언트-서버 구조를 가집니다. 
Docker 클라이언트는 Docker 컨테이너를 빌드, 실행 및 배포에 대한 무거운 작업을 수행하는 Docker 데몬과 통신을 합니다. Docker 클라이언트와 데몬은 동일한 시스템에서 실행되거나 Docker 클라이언트를 원격 Docker 데몬에 연결할 수 있습니다. 이런 Docker 클라이언트와 데몬은 UNIX 소켓 또는 네트워크 인터페이스를 통해 REST API를 사용하여 통신합니다. 

![도커의 구조](https://docs.docker.com/engine/images/architecture.svg)


### Docker daemon
Docker 데몬(dockerd)은 API 요청을 수신하고 이미지, 컨테이너, 네트워크 및 볼륨과 같은 Docker 객체를 관리합니다.

### Docker client
Docker 클라이언트는 많은 Docker 사용자가 Docker와 상호작용하는 기본방법 입니다. `docker run`과 같은 명령을 사용하면 클라이언트가 이러한 명령을 API로서 dockerd로 보내 실행합니다. 이때 Docker 클라이언트는 /var/run/docker.sock에 위치한 유닉스 소켓을 통해 도커 데몬의 API를 호출합니다. 도커 클라이언트가 사용하는 유닉스 소켓은 같은 호스트 내에 있는 도커 데몬에게 명령을 전달할 때 사용됩니다. tcp로 원격에 있는 도커 데몬을 제어하는 방법도 있습니다.

### Docker registries
Docker registries는 Docker 이미지를 저장합니다. Docker Hub는 누구나 사용할 수 있는 공용 레지스트리이며 Docker는 기본적으로 Docker Hub에서 이미지를 찾습니다.

<br>

간단하게 도커의 구조에 대해 알아보았습니다. 그렇다면 도커는 실제로 어디에 있는지 알아보기 위해 which 명령어를 이용하여 위치를 확인해보겠습니다.
```
# which docker
/usr/bin/docker
```
docker 명령어는 /usr/bin/docker에 위치한 파일을 통해 사용되고 있습니다. <br><br>


이번에는 실행 중인 도커 프로세스를 확인 해보겠습니다. 
```
# ps aux | grep docker
root     17594  0.0  0.1  14860  1064 pts/0    S+   09:19   0:00 grep --color=auto docker
root     21906  0.0  7.7 911984 77864 ?        Ssl  Mar07   0:54 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
```
컨테이너나 이미지를 다루는 명령어는 /usr/bin/docker에서 실행되지만 도커 엔진의 프로세스는 /usr/bin/dockerd 파일로 실행되고 있습니다. 이는 docker 명령어가 실제 도커 엔진이 아닌 클라이언트로서의 도커이기 떄문입니다.


<hr>


## 3. 도커 데몬 실행
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

## 4. 도커 데몬 모니터링

## 5. 스토리지 드라이버
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

### 5.1 스토리지 드라이버의 원리
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

#### 5.1.1 Copy-on-Write(CoW)
Copy-on-Write는 최대의 효율성을 위해 파일을 공유하고 복사하는 전략입니다. 이미지 내의 하위 레이어에 파일이나 디렉토리가 존재하고 최상위 레이어(쓰기 가능 계층)에 읽기 액세스가 필요한 경우 기존파일만 사용합니다. 반면 다른 레이어가 파일을 처음 수정해야 할 때 파일이 해당 레이어에 복사되고 수정됩니다.

![CoW](https://www.oreilly.com/library/view/getting-started-with/9781838645700/assets/dfc9cf05-7ad2-4f58-87a4-4702cd72dbbc.jpg)

### 5.2 overlayFS
OverlayFS는 다른 스토리지 드라이버와는 달리 계층화된 이미지 구조를 사용하지 않으며, lowedir이라는 단일화된 이미지 레이어를 사용합니다. 하위 디렉토리를 lowerdir로, 상위 디렉토리를 upperdir로 참조합니다. 통합 뷰는 merged라는 자체 디렉토리를 통해 노출됩니다.

![overlay구조](https://docs.docker.com/storage/storagedriver/images/overlay_constructs.jpg)


### overlay2 에서 컨테이너 저장공간 설정


참고자료 : <https://docs.docker.com/get-started/overview/><br>
참고자료 : <https://www.oreilly.com/library/view/getting-started-with/9781838645700/4da0f0db-5661-4599-91f7-53fc1ec62698.xhtml>

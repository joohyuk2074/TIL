# 도커 데몬

## 도커 데몬이란?
dockerd는 컨테이너를 관리하는 영구 프로세스입니다.

<hr>

## 도커의 구조
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


## 도커 데몬 실행
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

#### 2. 클라이언트 측에서 사용할 파일 생성

1. 클라이언트 측의 키 파일과 인증 요청 파일을 생성하고, extfile.cnf 파일에 extendedKeyUsage 항목을 추가합니다.


참고자료 : <https://docs.docker.com/get-started/overview/>

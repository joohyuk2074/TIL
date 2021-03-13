# 도커 데몬

## 도커 데몬이란?
dockerd는 컨테이너를 관리하는 영구 프로세스입니다. Docker는 데몬과 클라이언트에 다른 바이너리를 사용합니다. 데몬을 실행하려면 dockerd를 입력합니다.

<hr>

## 도커의 구조

Docker는 클라이언트-서버 구조를 가집니다. 
Docker 클라이언트는 Docker 컨테이너를 빌드, 실행 및 배포에 대한 무거운 작업을 수행하는 Docker 데몬과 통신을 합니다. Docker 클라이언트와 데몬은 동일한 시스템에서 실행되거나 Docker 클라이언트를 원격 Docker 데몬에 연결할 수 있습니다. 이런 Docker 클라이언트와 데몬은 UNIX 소켓 또는 네트워크 인터페이스를 통해 REST API를 사용하여 통신합니다. 

![도커의 구조](https://docs.docker.com/engine/images/architecture.svg)


### Docker daemon
Docker 데몬(dockerd)은 API 요청을 수신하고 이미지, 컨테이너, 네트워크 및 볼륨과 같은 Docker 객체를 관리합니다.

### Docker client
Docker 클라이언트는 많은 Docker 사용자가 Docker와 상호작용하는 기본방법 입니다. `docker run`과 같은 명령을 사용하면 클라이언트가 이러한 명령을 dockerd로 보내 실행합니다. `docker` 명령은 Docker API를 사용합니다. Docker 클라이언트는 둘 이상의 데몬과 통신 할 수 있습니다.

### Docker registries
Docker registries는 Docker 이미지를 저장합니다. Docker Hub는 누구나 사용할 수 있는 공용 레지스트리이며 Docker는 기본적으로 Docker Hub에서 이미지를 찾습니다.

<hr>

## 도커 데몬 실행

<hr>

## 도커 데몬 설정 


참고자료 : <https://docs.docker.com/get-started/overview/>

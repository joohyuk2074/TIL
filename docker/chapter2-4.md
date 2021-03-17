# 1. Dockerfile
Docker는 Dockerfile을 읽어서 이미지를 자동으로 빌드 할 수 있습니다.
Dockerfile은 사용자가 이미지를 생성하기 위해 명령 줄에서 호출할 수 있는 모든 명령이 포함된 텍스트 문서입니다. Docker 빌드를 사용하면 여러 명령 줄 지침을 연속적으로 실행하는 자동화 된 빌드를 만들 수 있습니다.
<br>

## 1.1 SpringBoot 애플리케이션을 Docker이미지로 빌드

### 1.1.1 프로젝트 생성
먼저 SpringInitializer를 통해 스프링부트 프로젝트를 생성하겠습니다.

```groovy
plugins {
    id 'org.springframework.boot' version '2.4.3'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}

```

### 1.1.2 프로젝트 작성
간단하게 프로젝트를 세팅합니다.
```java
package com.example.hellodocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HelloDockerApplication {

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloDockerApplication.class, args);
    }

}

```

### 1.1.3 프로젝트 빌드
해당 명령어를 실행하여 jar 파일을 생성하고 실행해 정상적으로 동작하는지 확인해 봅니다.
```
./gradlew build && java -jar build/libs/hello-docker-0.0.1-SNAPSHOT.jar
```


### 1.1.4 Dockerfile 작성 
프로젝트 최상단의 바로 아래에 간단한 Dockerfile을 작성합니다. 

```Dockerfile
FROM openjdk:11-jdk

WORKDIR /home/spring

COPY build/libs/*.jar /home/spring/application.jar

EXPOSE 8080

CMD ["java", "-jar", "/home/spring/application.jar"]
```

### 1.1.5 도커 이미지 빌드
작성한 Dockerfile을 토대로 이미지를 빌드합니다.
```
# docker build -t joohyuk/hello-docker:0.0 ./                        
```
-t 옵션은 생성될 이미지의 이름을 설정합니다. -t 옵션을 사용하지 않으면 16진수 형태의 이름으로 이미지가 저장되므로 -t 옵션을 사용하는 것이 좋습니다. 그리고 명령어의 끝에는 Dockerfile이 저장된 경로를 입력합니다. 외부 URL로 부터 Dockerfile의 내용을 가져와 빌드 할 수도 있지만 현재는 로컬에 Dockerfile을 저장으므로 ./(현재 디렉토리)를 입력했습니다.

```
[+] Building  (8/8) FINISHED                                                                                                                                                                                                                                              
 => [internal] load build definition from Dockerfile                                                                                                                                                                                                                    
 => => transferring dockerfile: 37B                                                                                                                                                                                                                                     
 => [internal] load .dockerignore                                                                                                                                                                                                                                      
 => => transferring context: 2B                                                                                                                                                                                                                                         
 => [internal] load metadata for docker.io/library/openjdk:11-jdk                                                                                                                                                                                                        
 => [1/3] FROM docker.io/library/openjdk:11-jdk@sha256:d112a532f300ce7d35f5cd196c22fced33d0a28a7dd0227019da5fb430528428                                                                                                                                               
 => [internal] load build context                                                                                                                                                                                                                                    
 => => transferring context: 121B                                                                                                                                                                                                                                     
 => CACHED [2/3] WORKDIR /home/spring                                                                                                                                                                                                                                  
 => CACHED [3/3] COPY build/libs/*.jar /home/spring/application.jar                                                                                                                                                                                                      
 => exporting to image                                                                                                                                                                                                                                                 
 => => exporting layers                                                                                                                                                                                                                                                 
 => => writing image sha256:5aa408a0318f9ed40a6e37e8861c779817751cf6d0421c8c38d68c90faf7efe9                                                                                                                                                                           
 => => naming to docker.io/joohyuk/hello-docker:0.0    
```

<br>

이미지가 생성되었는지 확인합니다.
```
# docker images
REPOSITORY             TAG       IMAGE ID       CREATED       SIZE
joohyuk/hello-docker   0.0       92be09e21ff8   2 hours ago   664MB
mysql                  latest    8457e9155715   2 weeks ago   546MB
```

<br>

### 1.1.6 컨테이너 생성
생성된 이미지를 이용하여 스프링부트 컨테이너를 생성합니다.
```
# docker run -d -p 8080:8080 --name myserver joohyuk/hello-docker:0.0
```

<br>

컨테이너가 생성된 것을 확인했습니다.
```
# docker ps                                                          
CONTAINER ID   IMAGE                      COMMAND                  CREATED          STATUS          PORTS                    NAMES
9c9776bbc6fb   joohyuk/hello-docker:0.0   "java -jar /home/spr…"   23 seconds ago   Up 22 seconds   0.0.0.0:8080->8080/tcp   myserver
```

<br>

스프링부트 애플리케이션이 제대로 동작하는지 `curl`명령어를 통해 확인해보겠습니다.
```
# curl localhost:8080             
Hello Docker World      
```

## 2. Dockerfile의 명령어

위에서 작성한 Dockerfile의 명령어에 대한 설명은 아래와 같습니다.

```Dockerfile
FROM openjdk:11-jdk
```
- FROM: 생성할 이미지의 베이스가 될 이미지를 뜻합니다. 새 빌드 단계를 초기화하고 다음 명령어에 대한 기본 이미지를 설정합니다. 여기서는 java11버전인 11-jdk를 베이스 이미지로 설정하였습니다. 

```Dockerfile
WORKDIR /home/spring
```
- WORKDIR: WORKDIR 명령어는 Dockerfile에서 WORKDIR이후의 RUN, CMD, ENTRYPOINT, COPY, ADD 명령어에 대한 작업 디렉토리를 설정합니다. 여기서는 /home/spring으로 설정하였습니다.

```Dockerfile
COPY build/libs/*.jar /home/spring/application.jar
```
- COPY: 로컬 디렉토리에서 읽어 들인 컨텍스트부터 이미지에 파일을 복사하는 역할을 합니다.

```Dockerfile
EXPOSE 8080
```
- EXPOSE: 컨테이너가 런타임에 지정된 네트워크 포트에서 수신 대기 함을 Docker에 알립니다. 포트가 TCP 혹은 UDP에서 수신하는지를 지정할 수 있으며 프로토콜이 지정되지 않았을 때 기본값은 TCP 입니다.
<br>
그러나 EXPOSE를 설정한 이미지로 컨테이너를 생성했다고 해서 반드시 이 포트가 호스트의 포트와 바인딩되는 것은 아니라 단지 8080포트를 사용할 것임을 나타내는 것뿐입니다. 컨테이너를 실행할 때 실제로 포트를 바인딩하려면 `docker run`명령에서 -p 옵션을 사용하여 하나 이상의 포트를 매핑시켜 줘야 합니다.

```Dockerfile
CMD ["java", "-jar", "/home/spring/application.jar"]
```
- CMD: CMD는 컨테이너가 시작될 때마다 실행할 명령어를 설정하며, Dockerfile에서 한 번만 사용할 수 있습니다.

더 많고 자세한 명령어는 [공식문서](https://docs.docker.com/engine/reference/builder/)를 참조하면 됩니다.
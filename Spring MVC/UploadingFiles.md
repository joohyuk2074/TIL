# 파일 업로드
Spring 공식문서를 참고하여 HTTP 멀티 파트 파일 업로드를 수신 할 수있는 서버 애플리케이션을 만드는 과정을 실습합니다.

## 목표
파일 업로드를 하는 Spring Boot 웹 애플리케이션을 생성합니다. 또한 간단한 html파일을 생성하여 테스트 파일을 업로드합니다.

<hr>

## 개발환경
- Java 8 이상
- Gradle 4+ 혹은 Maven 3.2+
- IntelliJ

<hr>

## 시작하기

Gradle로 SpringInitializer를 통해서 프로젝트를 생성해 줍니다.

```groovy
plugins {
    id 'org.springframework.boot' version '2.3.5.RELEASE'
    id 'io.spring.dependency-management' version '1.0.10.RELEASE'
    id 'java'
}

group = 'me.weekbelt'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
}

test {
    useJUnitPlatform()
}

```

Spring Boot는 MultipartConfigElement 빈을 생성하고 자체적으로 파일 업로드를 준비합니다. 따라서 따로 설정할 부분은 없습니다.

<hr>

## StorageService 생성
파일을 직접 저장하고 불러오기위한 비즈니스로직 처리를 위해 StorageService 인터페이스와 구현 클래스를 생성합니다.
```java
package me.weekbelt.demo.storage;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}

```
<br>

파일을 저장하거나 불러올때 예외처리를 위해 커스텀으로 예외클래스를 작성합니다.
```java
package me.weekbelt.demo.storage;

public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
package me.weekbelt.demo.storage;

public class StorageNotFoundException extends StorageException {
    public StorageNotFoundException(String message) {
        super(message);
    }

    public StorageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

```
<br>

저장 경로를 관리하기 위해 application.yml을 생성합니다.
```yml
storage:
  image:
  url: /Users/joohyuk/Pictures/
``` 

<br>

application.yml에 정의한 프로퍼티를 빈으로 생성하기 위해 @ConfigurationProperties를 이용하여 storage를 키값으로하는 빈을 생성합니다.
```java
package me.weekbelt.demo.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private String location = "upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

```

<br>

StorageService의 구현체를 생성합니다.
```java
package me.weekbelt.demo.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }

        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
```

<hr>

## FileUploadController 생성
```java
package me.weekbelt.demo;

import me.weekbelt.demo.storage.StorageFileNotFoundException;
import me.weekbelt.demo.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")        // 1
    public String listUploadFiles(Model model) {
        model.addAttribute("files",
                storageService.loadAll().map(
                        path -> {
                            UriComponentsBuilder serveFile = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString());
                            serveFile.build().toUri().toString();
                            return serveFile;
                        }
                ).collect(Collectors.toList()));
        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")             // 2
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
    
    @PostMapping("/")                           // 3
    public String handleFileUpload(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes) {
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";
    }
    
    @ExceptionHandler(StorageFileNotFoundException.class)       // 4
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
```

  1. listUploadFiles: StorageService에서 업로드된 파일의 현재 목록을 조회하여 Thymeleaf 템플릿에 로드합니다. MvcUriComponentsBuilder를 사용하여 실제 리소스에 대한 링크를 계산합니다.
  2. serveFile: 리소스를 로드하고 Content-Disposition 응답 헤더를 사용하여 다운로드 할 수 있도록 브라우저로 보냅니다.
  3. handleFileUpload: 파일을 업로드 합니다.

<hr>

## HTML 템플릿 생성
```html
<html xmlns:th="https://www.thymeleaf.org">
<body>

	<div th:if="${message}">
		<h2 th:text="${message}"/>
	</div>

	<div>
		<form method="POST" enctype="multipart/form-data" action="/">
			<table>
				<tr><td>File to upload:</td><td><input type="file" name="file" /></td></tr>
				<tr><td></td><td><input type="submit" value="Upload" /></td></tr>
			</table>
		</form>
	</div>

	<div>
		<ul>
			<li th:each="file : ${files}">
				<a th:href="${file}" th:text="${file}" />
			</li>
		</ul>
	</div>

</body>
</html>
```

<hr>

## 파일업로드 제한 조정
파일 업로드를 구성 할 때 파일 크기 제한을 설정하는 것이 유용한 경우가 많습니다. Spring Boot를 사용하면 일부 속성 설정으로 자동 구성된 MultipartConfigElement를 조정할 수 있습니다.

```yml
spring:
  servlet:
    multipart:
      max-file-size: 128KB
      max-request-size: 128KB
```


- max-file-size: 총 파일 크기는 128KB를 초과 할 수 없습니다.
- max-request-size: multipart/form-data의 총 요청 크기는 128KB를 초과 할 수 없습니다.


참고자료: https://spring.io/guides/gs/uploading-files/
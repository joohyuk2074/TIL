# JPA Auditing

## 일반적인 Auditing 구성

SpringData JPA는 Auditing 정보 캡처를 촉발하는데 사용할 수있는 엔티티 리스너와 함께 제공됩니다. 먼저 다음 예제와 같이 orm.xml 파일 내의 영속 컨텍스트에서 모든 엔티티에 사용할 AuditingEntityListener를 등록해야합니다.

```xml
<persistence-unit-metadata>
  <persistence-unit-defaults>
    <entity-listeners>
      <entity-listener class="….data.jpa.domain.support.AuditingEntityListener" />
    </entity-listeners>
  </persistence-unit-defaults>
</persistence-unit-metadata>
```

<br>

또한 다음과 같이 @EntityListeners 애노테이션을 사용하여 엔티티별로 AuditingEntityListener를 활성화 할 수 있습니다.

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class MyEntity {

}
```

<br>
SpringData JPA 1.5부터 @EnableJpaAuditing 애노테이션으로 구성 클래스에 Auditing을 활성화 할 수 있습니다.

```java
@Configuration
@EnableJpaAuditing
class Config {

  @Bean
  public AuditorAware<AuditableUser> auditorProvider() {
    return new AuditorAwareImpl();
  }
}
```

참고 자료: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference
# @AuthenticationPrincipal
Spring Security는 현재 Authentication.getPrincipal()을 자동으로 해결할 수있는 AuthenticationPrincipalArgumentResolver를 제공합니다. @EnableWebSecurity를 사용하면 자동으로 Spring MVC 구성에 추가됩니다. XML 기반 구성을 사용하는 경우 아래와 같이 직접 추가해야합니다.

```xml
<mvc:annotation-driven>
        <mvc:argument-resolvers>
                <bean class="org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver" />
        </mvc:argument-resolvers>
</mvc:annotation-driven>
```
<br>

개발자가 직접 만든 UserDetail 타입의 CustomUser 객체를 반환하는 UserDetailsService가있는 상황을 생각해 보겠습니다. 현재 인증 된 사용자의 CustomUser는 다음 코드를 사용하여 액세스 할 수 있습니다.

```java
@RequestMapping("/messages/inbox")
public ModelAndView findMessagesForUser() {
    Authentication authentication =
    SecurityContextHolder.getContext().getAuthentication();
    CustomUser custom = (CustomUser) authentication == null ? null : authentication.getPrincipal();

    // 이 사용자에 대한 메시지를 찾아서 반환 ...
}
```

<br>

Spring Security 3.2부터 우리는 @AuthenticationPrincipal 어노테이션을 파라미터에 추가함으로써 보다 직관적으로 인증객체의 Principal을 얻을 수 있습니다.

```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// ...

@RequestMapping("/messages/inbox")
public ModelAndView findMessagesForUser(@AuthenticationPrincipal CustomUser customUser) {

    // .. find messages for this user and return them ...
}
```

<br>

때때로 어떤 방식 으로든 Principal을 변환해야 할 수도 있습니다. 예를 들어 CustomUser가 최종 사용자 여야하는 경우 확장 할 수 없습니다. 이 상황에서 UserDetailsService는 UserDetails를 구현하고 CustomUser에 액세스 할 수있는 getCustomUser라는 메서드를 제공하는 개체를 반환 할 수 있습니다.

```java
public class CustomUserUserDetails extends User {
        // ...
        public CustomUser getCustomUser() {
                return customUser;
        }
}
```

<br>

그런 다음 Authentication.getPrincipal()을 루트 객체로 사용하는 SpEL 표현식을 사용하여 CustomUser에 접근 할 수 있습니다.

```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// ...

@RequestMapping("/messages/inbox")
public ModelAndView findMessagesForUser(@AuthenticationPrincipal(expression = "customUser") CustomUser customUser) {

    // .. 이 사용자에 대한 메시지를 찾아 반환 ...
}
```

<br>

SpEL 표현식에서 Bean을 참조 할 수도 있습니다. 예를 들어, JPA를 사용하여 사용자를 관리하고 현재 사용자의 속성을 수정하고 저장하려는 경우 다음을 사용할 수 있습니다.
```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// ...

@PutMapping("/users/self")
public ModelAndView updateName(@AuthenticationPrincipal(expression = "@jpaEntityManager.merge(#this)") CustomUser attachedCustomUser,
        @RequestParam String firstName) {

    attachedCustomUser.setFirstName(firstName);

    // ...
}
```

<br>

@AuthenticationPrincipal을 @CurrentUser라는 커스텀에노테이션을 생성하여 사용할 수 있다.
```java
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {}
```

<br>

이제 @CurrentUser를 통해 CustomUser객체에 접근할 수 있습니다.
```java
@RequestMapping("/messages/inbox")
public ModelAndView findMessagesForUser(@CurrentUser CustomUser customUser) {

    // .. find messages for this user and return them ...
}
```

<br><br>

참고자료: https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#mvc-authentication-principal
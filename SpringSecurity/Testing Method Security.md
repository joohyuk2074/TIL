# 인증된 사용자가 접근할 수 있는 기능 테스트하기

## @WithMockUser

통합 테스트를 진행할 때 인증이 된 상태에서 테스트를 진행해야 하는경우가 있는데 특정 사용자로 테스트를 쉽게 진행할 수 있는 방법이 @WithMockUser를 사용하는 것입니다. @WithMockUser를 사용하면 사용자 이름이 "user", 비밀번호가 "password", Role이 "ROLE_USER"의 기본 값으로 가진 사용자가 인증했다고 가정하고 실행됩니다.

```java
@Test
@WithMockUser
public void getMessageWithMockUser() {
String message = messageService.getMessage();
...
}
```

- SecurityContext에 존재하는 Authentication은 UsernamePasswordAuthenticationToken 유형입니다. 
- 인증의 주체는 Spring Security의 User 객체입니다.
- 사용자는 사용자 이름 "user", 암호 "password"를 가지며 "ROLE_USER"라는 단일 GrantedAuthority가 사용됩니다.

사용자 이름을 바꾸고 싶으면 다음과 같이 설정하면 됩니다.
```java
@Test
@WithMockUser("customUsername")
public void getMessageWithMockUserCustomUsername() {
    String message = messageService.getMessage();
...
}
```

Role도 지정할 수 있습니다.
```java
@WithMockUser(username="admin",roles={"USER","ADMIN"})
public void getMessageWithMockUserCustomUser() {
    String message = messageService.getMessage();
    ...
}

```

<hr>

## @WithAnonymousUser
@WithAnonymousUser를 사용하면 익명 사용자로 실행할 수 있습니다. 이것은 특정 사용자로 대부분의 테스트를 실행하고 익명 사용자로 몇 가지 테스트를 실행하려는 경우 특히 편리합니다.

```java
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockUser
public class WithUserClassLevelAuthenticationTests {

    @Test
    public void withMockUser1() {
    }

    @Test
    public void withMockUser2() {
    }

    @Test
    @WithAnonymousUser
    public void anonymous() throws Exception {
        // override default to run as anonymous user
    }
}
```

기본적으로 SecurityContext는 TestExecutionListener.beforeTestMethod 이벤트 중에 설정됩니다. 이것은 JUnit의 @Before 이전에 발생하는 것과 동일합니다.

<br>

JUnit의 @Before 이후이지만 테스트 메소드가 호출되기 전에 TestExecutionListener.beforeTestExecution 이벤트 중에 발생하도록 변경할 수 있습니다.

```java
@WithAnonymousUser(setupBefore = TestExecutionEvent.TEST_EXECUTION)
```

<hr>

## @WithUserDetails
UserDetailsService가 빈으로 등록되어 있다고 가정하고 다음 테스트는 사용자 이름이 "user"인 UserDetailsService에서 반환되는 Authentication 객체의 타입인 UsernamePasswordAuthenticationToken과 principal이 함께께 호출됩니다.

```java
@Test
@WithUserDetails
public void getMessageWithUserDetails() {
    String message = messageService.getMessage();
    ...
}
```
<br>

UserDetailsService에서 사용자를 조회하는 데 사용되는 사용자 이름을 사용자 지정할 수도 있습니다. 예를 들어,이 테스트는 사용자 이름이 "customUsername"인 UserDetailsService에서 반환 된 principal로 실행됩니다.
```java
@Test
@WithUserDetails("customUsername")
public void getMessageWithUserDetailsCustomUsername() {
    String message = messageService.getMessage();
    ...
}
```

<br>

UserDetailsService를 조회하기 위해 명시적인 빈 이름을 제공 할 수도 있습니다. 예를 들어,이 테스트는 빈 이름이 "myUserDetailsService"인 UserDetailsService를 사용하여 "customUsername"의 사용자 이름을 조회합니다.
```java
@Test
@WithUserDetails(value="customUsername", userDetailsServiceBeanName="myUserDetailsService")
public void getMessageWithUserDetailsServiceBeanName() {
    String message = messageService.getMessage();
    ...
}
```

<br>

@WithMockUser처럼 모든 테스트에서 동일한 사용자를 사용하도록 클래스 수준에 주석을 배치 할 수도 있습니다. 그러나 @WithMockUser와 달리 @WithUserDetails는 사용자가 존재해야합니다.

<br>

기본적으로 SecurityContext는 TestExecutionListener.beforeTestMethod 이벤트 중에 설정됩니다. 이것은 JUnit의 @Before 이전에 발생한다는 뜻입니다. JUnit의 @Before 이후이지만 테스트 메소드가 호출되기 전에 TestExecutionListener.beforeTestExecution 이벤트 중에 발생하도록 변경할 수 있습니다.

```java
@WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
```

<hr>

## @WithSecurityContext
사용자 지정 인증 주체를 사용하지 않는 경우 @WithMockUser가 탁월한 선택임을 확인했습니다. 다음으로 @WithUserDetails는 사용자 지정 UserDetailsService를 사용하여 인증 주체를 만들 수 있지만 사용자가 있어야한다는 것을 발견했습니다. 이제 가장 융통성있는 옵션을 볼 수 있습니다.
<br>
@WithSecurityContext를 사용하여 원하는 SecurityContext를 생성하는 커스텀애노테이션을 생성할 수 있습니다.

```java
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default "rob";

    String name() default "Rob Winch";
}
```

<br>
@WithSecurityContext 어노테이션은 @WithMockCustomUser 어노테이션이 주어지면 새로운 SecurityContext를 생성 할 SecurityContextFactory를 지정해야합니다.

```java
public class WithMockCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        CustomUserDetails principal =
            new CustomUserDetails(customUser.name(), customUser.username());
        Authentication auth =
            new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
```

<br>

이제 새로운 어노테이션으로 테스트 클래스 또는 테스트 메소드에 어노테이션을 작성할 수 있으며 Spring Security의 WithSecurityContextTestExecutionListener는 SecurityContext가 적절하게 채워지는지 확인합니다.

<br>
WithSecurityContexstFactory의 구현체를 만들때 스프링 빈을 주입받을 수 있습니다.

```java
final class WithUserDetailsSecurityContextFactory
    implements WithSecurityContextFactory<WithUserDetails> {

    private UserDetailsService userDetailsService;

    @Autowired
    public WithUserDetailsSecurityContextFactory(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public SecurityContext createSecurityContext(WithUserDetails withUser) {
        String username = withUser.value();
        Assert.hasLength(username, "value() must be non-empty String");
        UserDetails principal = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
```

<br>

@WithSecurityContext도 기본적으로 SecurityContext를 TestExecutionListener.beforeTestMethod 이벤트 중에 설정됩니다. JUnit의 @Before 이후, 테스트 메소드가 호출되기 전에 TestExecutionListener.beforeTestExecution 이벤트 중에 발생하도록 변경할 수 있습니다.

```java
@WithSecurityContext(setupBefore = TestExecutionEvent.TEST_EXECUTION)
```


참고자료: https://docs.spring.io/spring-security/site/docs/current/reference/html5/
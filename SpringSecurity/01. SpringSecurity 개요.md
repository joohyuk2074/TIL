# 1. 스프링 시큐리티(Spring Security) 란?
#### 스프링 시큐리티는 스프링 기반의 어플리케이션의 보안(인증과 권한)을 담당하는 프레임워크를 말합니다. 만약 스플이 시큐리티를 사용하지 않는다면, 자체적으로 세션을 체크하고 리다이렉트 등을 해야 할 것입니다. 스프링 시큐리티는 필터(Filter) 기반으로 동작하기 때문에 스프링 MVC 와 분리되어 관리 및 동작합니다.

스프링 시큐리티를 사용하기 전에 관련 배경 지식을 알아보겠습니다.

* 접근 주체(Principal): 보호된 대상에 접근하는 유저
* 인증(Authentication): 인증은 '증명하다'라는 의미로 예를 들어, 유저 아이디와 비밀번호를 이용하여 로그인 하는 과정을 말합니다.
* 인가(Authorization): '권한부여'나 '허가'와 같은 의미로 사용됩니다. 즉, 어떤 대상이 특정 목적을 실현하도록 허용(Acees) 하는 것을 의미합니다.
* 권한: 인증된 주체가 애플리케이션의 동작을 수행할 수 있도록 허락되었는지를 결정할 때 사용합니다.
<br></br>

# 2.스프링 시큐리티 필터(Spring Security Filter)
![Spring Security Filter](/images/Spring%20Security%20Filter.png)   
그림 출처 : https://youmekko.github.io/2018/04/26/2018-04-26-Filter/   

클라이언트(보통 브라우저)는 요청을 보내게 되고, 그 요청을 서블릿이나 JSP등이 처리하게 됩니다.   
스프링 MVC에서는 요청을 가장 먼저 받는 것이 DispatcherServlet이라고 했었습니다. 이 DisplatcherServlet이 요청 받기 전에 다양한 필터들이 있을 수 있습니다.   

먼저 스프링 시큐리티가 제공하는 필터들이 어떤 역할을 담당하는지 정리 해보겠습니다.   

![Spring Security Filter](/images/SecurityFilterChain.png)   
그림 출처 : https://atin.tistory.com/590   

위 그림은 시큐리티 필터 체인과 각각의 필터에서 사용하는 객체들(Repository, Handler, Manager등)에 대해 잘 표현하고 있습니다.   

* SecurityContextPersistenceFilter: SecurityContextRepository에서 SecurityContext를 가져오거나 저장하는 역할을 한다.
* LogoutFilter: 설정된 로그아웃 URL로 오는 요청을 감시하며, 해당 유저를 로그아웃 처리
* (UsernamePassword)AuthenticationFilter: (아이디와 비밀번호를 사용하는 form 기반 인증) 설정된 로그인 URL로 오는 요청을 감시하며, 유저 인증 처리
    1. AuthenticationManager를 통한 인증 실행
    2. 인증 성공 시, 얻은 Authentication 객체를 SecurityContext에 저장 후 AuthenticationSuccessHandler 실행
    3. 인증 실패 시, AuthenticationFailureHandler 실행
* DefaultLoginPageGeneratingFilter: 인증을 위한 로그인폼 URL을 감시한다.
* BasicAuthenticationFilter: HTTP 기본 인증 헤더를 감시하여 처리한다.
* RequestCacheAwareFilter: 로그인 성공 후, 원래 요청 정보를 재구성하기 위해 사용된다.
* SecurityContextHolderAwareRequestFilter: HttpServletRequestWrapper를 상속한 SecurityContextHolderAwareRequestWrapper 클래스로 HttpServletRequest 정보를 감싼다. SecurityContextHolderAwareRequestWrapper 클래스는 필터 체인상의 다음 필터들에게 부가정보를 제공한다.
* AnonymousAuthenticationFilter: 이 필터가 호출되는 시점까지 사용자 정보가 인증되지 않았다면 인증 토큰에 사용자가 익명 사용자로 나타난다.
* SessionManagementFilter: 이 필터는 인증된 사용자와 관련된 모든 세션을 추적한다.
* ExceptionTransaltionFilter: 이 필터는 보호된 요청을 처리하는 중에 발생할 수 있는 예외를 위임하거나 전달하는 역할을 한다.
* FilterSecurityInterceptor: 이 필터는  AccessDecisionManager로 권한 부여 처리를 위임함으로써 접근 제어 결정을 쉽게 해준다.
<br></br>

# 3. 스프링 시큐리티 인증관련 아키텍처
아이디와 암호를 입력했을 때 이를 처리하는 필터는 AuthenticationFilter입니다.   
해당 필터는 다음 그림과 같은 순서로 동작합니다.   
   
![SpringSecurityArchitecture](/images/SpringSecurityArchitecture.png)   
그림 출처: http://www.springbootdev.com   

1. 클라이언트가(유저)가 로그인을 시도합니다.
2. AuthenticationFilter는 AuthenticationManager, AuthenticationProvider(s), UserDetailsService를 통해 DB에서 사용자 정보를 읽어옵니다. 여기서 중요한 것은 UserDetailService가 인터페이스라는 것입니다. 해당 인터페이스를 구현한 빈(Bean)을 생성하면 스프링 시큐리티는 해당 빈을 사용하게 됩니다. 즉, 어떤 데이터베이스로 부터 읽어들일지 스프링 시큐리티를 이용하는 개발자가 결정할 수 있게 됩니다.
3. UserDetailsService는 로그인한 ID에 해당하는 정보를 DB에서 읽어 들여 UserDetails를 구현한 객체로 반환합니다. 프로그래머는 UserDetail를 구현한 객체를 만들어야 할 필요가 있을 수 있습니다. UserDetails 정보를 세션에 저장하게 됩니다.
4. 스프링 시큐리티는 인메모리 세션저장소인 SecurityContextHolder에 UserDetails정보를 저장하게 됩니다.
5. 클라이언트(유저)에게 session ID(JSESSION ID)와 함께 응답을 하게 됩니다.
6. 이후 요청에서는 요청 쿠키에서 JSESSION ID 정보를 통해 이미 로그인 정보가 저장되어 있는 지 확인합니다. 이미 저장되어 있고 유효하면 인증 처리를 해주게 됩니다.
<br></br>

참고자료: https://www.edwith.org/boostcourse-web-be/lecture/58997/
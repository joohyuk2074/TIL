## 인터셉터(Interceptor)란?
Interceptor는 Displatcher servlet에서 Handler(Controller)로 요청을 보낼 때, Handler에서 Dispatcher servlet으로 응답을 보낼 때 동작합니다.

![Intercpetor](/images/Interceptor.png)

<hr>

## 인터셉터 작성법
* org.springframework.web.servlet.HandlerInterceptor 인터페이스를 구현합니다.
* org.springframework.web.servlet.handler.HandlerInterceptorAdapter 클래스를 상속 받습니다.
* Java Config를 사용한다면, WebMvcConfigurerAdapter가 가지고 있는 addInterceptors 메소드를 오버라이딩하고 등록하는 과정을 거칩니다.

<hr>

## 인터셉터 적용

```java
package kr.or.connect.guestbook.interceptor;

public class LogInterceptor extends HandlerInterceptorAdapter{

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println(handler.toString() + " 가 종료되었습니다.  " + modelAndView.getViewName() + "을 view로 사용합니다.");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println(handler.toString() + " 를 호출했습니다.");
		return true;
	}

}
```
1. HandlerInterceptorAdapter클래스를 상속받아 인터셉터를 생성합니다.
2. 핸들러 처리전에 처리할 preHandler메소드와 핸들러 처리후 처리하는 postHandle메소드를 오버라이딩 합니다.

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LogInterceptor());
}
```
3. WebMvcConfigurerAdapter를 상속받는 Configuration클래스에 addInterceptors 메소드를 오버라이딩하여 작성한 LogInterceptor를 추가합니다.


참고 자료1 : https://www.edwith.org/boostcourse-web-be/lecture/59001/
<br>
참고 자료2 : https://www.edwith.org/boostcourse-web-be/lecture/59002/
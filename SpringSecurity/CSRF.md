## 1. CSRF 란?

은행 웹 사이트가 현재 로그인한 사용자로부터 다른 은행 계좌로 돈을 송금할 수 있는 양식을 제공한다고 가정해 봅니다. 예를 들어, 양도 양식은 다음과 같을 수 있습니다. 

```html
<form method="post"
    action="/transfer">
<input type="text"
    name="amount"/>
<input type="text"
    name="routingNumber"/>
<input type="text"
    name="account"/>
<input type="submit"
    value="Transfer"/>
</form>
```
<br>

해당하는 HTTP 요청은 다음과 같을 수 있습니다.
```http
POST /transfer HTTP/1.1
Host: bank.example.com
Cookie: JSESSIONID=randomid
Content-Type: application/x-www-form-urlencoded

amount=100.00&routingNumber=1234&account=9876
```
<br>

이제 은행 웹 사이트를 인증한 다음 로그아웃 하지 않고 다음과 같은 형식의 HTML 페이지를 방문하게 된다면
```html
<form method="post"
    action="https://bank.example.com/transfer">
<input type="hidden"
    name="amount"
    value="100.00"/>
<input type="hidden"
    name="routingNumber"
    value="evilsRoutingNumber"/>
<input type="hidden"
    name="account"
    value="evilsAccountNumber"/>
<input type="submit"
    value="Win Money!"/>
</form>
```
<br>

돈을 준다는 메시지버튼을 보고 그 버튼을 클릭했다면, 이 과정에서 의도하지 않게 100달러를 악의적인 사용자에게 송급하게 됩니다. 악의적인 웹사이트는 피해자의 쿠키를 볼 수 없지만 은행과 관련된 쿠키는 여전히 요청과 함꼐 보내지기 때문입니다.

<br>
최악의 경우, 이 모든 과정이 자바스크립트를 사용하여 버튼을 클릭할 필요도 없이 자동화되엇을 수 있습니다. 

<hr>

## 2. CSRF 공격으로 부터 보호
CSRF 공격이 가능한 이유는 피해자 웹 사이트의 HTTP 요청과 공격자 웹 사이트의 요청이 정확히 동일하기 때문입니다.
즉, 악의적 인 웹 사이트에서 오는 요청을 거부하고 은행 웹 사이트에서 오는 요청을 허용 할 방법이 없습니다. CSRF 공격으로부터 보호하려면 요청에 악의적인 사이트가 제공 할 수없는 것이 있는지 확인하여 두 요청을 구분할 수 있어야합니다.

<br>

Spring은 CSRF 공격으로부터 보호하기 위해 두 가지 메커니즘을 제공합니다.
- Synchronizer Token 패턴
- 세션 쿠키에 SameSite속성 지정

### Synchronizer Token 패턴
CSRF 공격으로부터 보호하는 가장 보편적이고 방법은 Synchronizer Token 패턴을 사용하는 것입니다. 이 방법은 각 HTTP 요청에 세션 쿠키 외에 CSRF 토큰이라는 안전한 임의 생성 값이 HTTP 요청에 있어야 함을 확인하는 것입니다. 
<br><br>
HTTP 요청이 제출되면 서버는 예상되는 CSRF 토큰을 찾아 HTTP 요청의 실제 CSRF 토큰과 비교해야합니다. 값이 일치하지 않으면 HTTP 요청을 거부해야합니다.
<br><br>
이 작업의 핵심은 실제 CSRF 토큰이 브라우저에 의해 자동으로 포함되지 않는 HTTP 요청의 일부에 있어야한다는 것입니다. 예를 들어, HTTP 파라미터 또는 HTTP 헤더에 실제 CSRF 토큰을 요구하면 CSRF 공격으로부터 보호됩니다.
쿠키는 브라우저의 HTTP 요청에 자동으로 포함되므로 쿠키에 실제 CSRF 토큰을 요구하는 것은 작동하지 않습니다.
<br><br>
애플리케이션 상태를 업데이트하는 각 HTTP 요청에 대해 실제 CSRF 토큰만 요구하도록 할 수 있습니다. 이것이 작동하려면 애플리케이션이 안전한 HTTP 메소드가 멱 등성을 보장해야합니다.
<br><br>
Synchronizer Token Pattern을 사용할 때 예제가 어떻게 변경되는지 살펴 보겠습니다. 실제 CSRF 토큰이 _csrf라는 HTTP 파라미터에 있어야한다고 가정합니다. 신청서의 이전 양식은 다음과 같습니다.

```html
<form method="post"
    action="/transfer">
<input type="hidden"
    name="_csrf"
    value="4bfd1575-3ad1-4d21-96c7-4ef2d9f86721"/>
<input type="text"
    name="amount"/>
<input type="text"
    name="routingNumber"/>
<input type="hidden"
    name="account"/>
<input type="submit"
    value="Transfer"/>
</form>
```

이제 양식에 CSRF 토큰 값이있는 숨겨진 입력이 포함됩니다. 동일한 출처 정책이 악의적 사이트가 응답을 읽을 수 없도록 보장하기 때문에 외부 사이트는 CSRF 토큰을 읽을 수 없습니다.

<br>
송금을위한 해당 HTTP 요청은 다음과 같습니다.

```http
POST /transfer HTTP/1.1
Host: bank.example.com
Cookie: JSESSIONID=randomid
Content-Type: application/x-www-form-urlencoded

amount=100.00&routingNumber=1234&account=9876&_csrf=4bfd1575-3ad1-4d21-96c7-4ef2d9f86721
```

이제 HTTP 요청에 랜덤값을 포함한 _csrf 매개 변수가 포함되어 있음을 알 수 있습니다.
공격자의 웹 사이트는 _csrf 매개 변수에 대한 올바른 값을 제공 할 수 없으며 서버가 실제 CSRF 토큰을 예상 된 CSRF 토큰과 비교할 때 전송이 실패합니다.


### CSRF 보호를 사용하는 경우
일반 사용자가 브라우저에서 처리 할 수있는 모든 요청에 대해 CSRF 보호를 사용하는 것이 좋습니다.
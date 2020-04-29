# 1. CSRF 란?
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
```
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
# Email
## 1. 사용법
다음 예제와 같이 OrderManager라는 인터페이스가 있다고 하고 주문번호가 포함된 이메일 메시지를 생성하여 주문을 한 고객에게 관련 정보를 보내야 한다고 가정하겠습니다.

```java
public interface OrderManager {

    void placeOrder(Order order);

}
```

### MainSender와 SimpleMailMessage의 사용법
아래 코드는 누군가 주문할 때 MailSender 및 SimpleMailMessage를 사용하여 이메일을 보내는 방법을 보여줍니다.

```java
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SimpleOrderManager implements OrderManager {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    public void placeOrder(Order order) {

        // 비즈니스 로직 ...

        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(order.getCustomer().getEmailAddress());
        msg.setText(
            "Dear " + order.getCustomer().getFirstName()
                + order.getCustomer().getLastName()
                + ", thank you for placing order. Your order number is "
                + order.getOrderNumber());
        try{
            this.mailSender.send(msg);
        }
        catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
```
<hr>

## MimeMessageHelper 사용
MimeMessageHelper를 사용하면 JavaMail메시지를 처리 할 때 매우 편리합니다.

```java
JavaMailSenderImpl sender = new JavaMailSenderImpl();
sender.setHost("mail.host.com");

MimeMessage message = sender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message);
helper.setTo("test@host.com");
helper.setText("Thank you for ordering!");

sender.send(message);
```

### 첨부파일 및 Inline Resource 보내기
멀티 파트 이메일 메시지는 첨부 파일과 인라인 리소스를 모두 허용합니다. 인라인 리소스의 예로는 메시지에 사용하고 싶지만 첨부 파일로 표시하지 않고 html 이미지 태그의 src속성 같은 이미지 또는 스타일 시트를 말합니다.

#### 첨부파일
아래 코드는 MimeMessageHelper를 사용하여 JPEG 이미지 첨부파일을 보내는 방법을 보여줍니다.

```java
JavaMailSenderImpl sender = new JavaMailSenderImpl();
sender.setHost("mail.host.com");

MimeMessage message = sender.createMimeMessage();

// 멀티파트 메시지를 보낼때 true로 설정해야 한다.
MimeMessageHelper helper = new MimeMessageHelper(message, true);
helper.setTo("test@host.com");

helper.setText("Check out this image!");

// 로컬에 있는 이미지 파일
FileSystemResource file = new FileSystemResource(new File("c:/Sample.jpg"));
helper.addAttachment("CoolImage.jpg", file);

sender.send(message);

```

#### Inline Resource

아래 코드는 MimeMessageHelper를 사용하여 인라인 이미지로 이메일을 보내는 방법을 보여줍니다.

```java
JavaMailSenderImpl sender = new JavaMailSenderImpl();
sender.setHost("mail.host.com");

MimeMessage message = sender.createMimeMessage();

// use the true flag to indicate you need a multipart message
MimeMessageHelper helper = new MimeMessageHelper(message, true);
helper.setTo("test@host.com");

// use the true flag to indicate the text included is HTML
helper.setText("<html><body><img src='cid:identifier1234'></body></html>", true);

// let's include the infamous windows Sample file (this time copied to c:/)
FileSystemResource res = new FileSystemResource(new File("c:/Sample.jpg"));
helper.addInline("identifier1234", res);

sender.send(message);
```

### 템플릿 라이브러리를 사용하여 이메일 메시지 생성
이전 예제의 코드는 message.setText (..)와 같은 메서드 호출을 사용하여 이메일 메시지의 내용을 명시 적으로 만들었습니다.
<br>
이는 간단한 경우에 적합하며 API의 기본 사항을 보여 주려는 의도가있는 앞서 언급 한 예제의 맥락에서 괜찮습니다.
<br><br>
그러나 실무에서 개발자는 여러 가지 이유로 이전에 표시된 접근 방식을 사용하여 이메일 메시지의 내용을 만들지 않는 경우가 많습니다.

- Java 코드로 HTML 기반 이메일 콘텐츠를 만드는 것은 지루하고 오류가 발생하기 쉽습니다.
- 디스플레이 로직과 비즈니스 로직 사이에는 명확한 구분이 없습니다.
- 이메일 컨텐츠의 표시 구조를 변경하려면 Java 코드 작성, 재 컴파일, 재배포 등이 필요합니다.

일반적으로 이러한 문제를 해결하기 위해 취한 접근 방식은 템플릿 라이브러리 (예 : FreeMarker)를 사용하여 이메일 콘텐츠의 표시 구조를 정의하는 것입니다.
<br>
이렇게하면 이메일 템플릿에서 렌더링 할 데이터를 만들고 이메일을 보내는 작업 만 코드에 할당됩니다.
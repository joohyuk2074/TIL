# 실천 할 수 있는 컨벤션 교정
## 1. 이름
### 1.1 Java 표기법
- 변수 이름: camelCase
- 함수 이름: camelCase
- 클래스 이름: PascalCase
- 패키지 이름: allowercase
- 상수: UPPER_SNAKE_CASE
### 1.2 헝가리안 표기법
- 변수가 자연스럽게 읽히지 않음
- IDE가 시원치 않던 시절의 구시대 유물이기 때문에 권장하지 않음
- bVariable, g_variable, iVariable, m_variable, strVariable, s_variable
### 1.3 줄여쓰기
아래와 같이 가능하면 풀어써서 코드베이스의 일관성을 지킨다.
- msg -> message
- prj -> project
- obj -> object
- ws -> webSocket
- ws -> webServer
코드를 작성하고 나서 나중에 보면 뭔지 알 수 없다. ws같은경우는 webSocket인지 webServer인지 구분을 할 수 없다. id, app은 관용적으로 많이 표현하므로 웬만하면 풀어쓰되 허용하는 몇 개의 단어를 정의한다.
### 1.4 축약어
축약어는 일반 명사와 같은 취급을 하므로 대문자로 표현하지 않는다.
- userID(x) -> userId(0)
### 1.5 Simple / Light / Base
- 클래스이름을 유의미하게 목적에 맞는 이름으로 변경하라.
- Util이라는 클래스명도 가급적 삼가해야한다. Util을 붙이는 순간 이 클래스에 온갖 static 메서드가 모이게 된다. 아래와 같이 객체를 생성하는 경우는 팩토리로 표현하는게 더 좋다.
    
    ```java
    // bad
    class ApplicationUtil {
        public static Application create() {
            return new Application();
        }
    }
    ```

    ```java
    // good
    class ApplicationFactory {
        public static Application create() {
            return new Application();
        }
    }
    ```

## 2. 동사
### 2.1 get vs find
get과 find를 구분해서 써야한다.
- get: return type이 T 인 경우 (일반적으로 get이라고 하면 항상 인스턴스를 돌려받는 다는 의미기 때문에 데이터가 없을 시 exception을 throw 한다.)
- find: return type이 Optional<T>인 경우
### 2.2 isExists vs exist
- isExist는 동사를 반복하기때문에 없는단어다. exist를 사용하라.
### 2.3 get
get 접두어는 갖고 있는 속성 정보를 제공한다는 의미입니다. 찾아오라는 지시가 아닙니다. 따라서 get을 남발하지 않습니다.

모든 상품 가격을 더하는 메서드가 있다고 할 때 getSumPrice보다는 sumPrice가 더 권장된다.
```java
class Products {
    public int getSumPrice() {  // 잘못된 메서드명
        return this.products.stream().mapToLong(Product::getPrice).sum();
    }

    public int sumPrice() {     // 올바른 메서드명
        return this.products.stream().mapToLong(Product::getPrice).sum();
    }
}
```
## 3. 롬복
### 3.1 getter, setter
- getter, setter를 남발하면 객체를 수동적으로 만들게 되고 사실살 public 멤버변수를 선언하는거나 다름없다.
- 캡슐화를 망치는 주범이기 때문에 남발하지 않는다.

#### 수동적인 객체
```java
@Getter
@Setter
class User {

    enum Status { ACTIVE, INACTIVE }

    private long id;
    private String name;
    private Status status;
    private long lastLoginTimestamp;
}
```
```java
class UserManager {
    public void doSomething(User user) {
        user.setStatus(Status.ACTIVE);
        user.setLastLoginTimestamp(Clock.systemUTC().millis());
    }
}
```

#### 능동적인 객체
```java
@Getter
@Setter
class User {

    enum Status { ACTIVE, INACTIVE }

    private long id;
    private String name;
    private Status status;
    private long lastLoginTimestamp;

    public void inactive() {
        this.status = Status.INACTIVE;
    }

    public void login(Clock clock) {
        this.lastLoginTimestamp = clock.millis();
    }
}
```
```java
class UserManager {
    public void doSomething(User user) {
        user.inactive();
        user.login(Clock.systemUTC())
    }
}
```
객체를 조금 더 능동적이게 만드려면 객체한테 일을 시켜야 한다. 이것을 TDA(Tell don't ask)원칙 이라고 한다.

## 4. 가독성
### 4.1 주석
- 주석은 정말 필요할 때만 사용한다.
### 4.2 Optional
- Optional을 자주 사용하라. 코드의 완성도를 높이고 런타임 에러(NPE)를 줄여준다.
### 4.3 Collection.Map
- Collection.Map을 남발하지 않는다.
- 가급적이면 일급 클래스로 만들고 사용하더라도 지정된 { scope } 밖을 넘나들지 않는다.
- Map을 사용하면 본인만 이해할 수 있는 코드가 될 확률이 높다.
    ```java 
    // Bad
    Map<Long, List<LectureComment>> userIdToLectureComments = new HashMap<>();
    ```
## 5. 관습
### 5.1 start, end
- 어떤 범위를 표현할 때 보통 시작은 포함시키고 끝은 제외시킨다. range는 [start, end)
```java
@Contract(pure = true) @NotNull
public static IntStream range(int startInclusive, int endExclusive) {
    if (startInclusive >= endInclusive) {
        return empty();
    } else {
        return StreamSupport.intStream(
            new Streams.RangeIntSpliterator(startInclusive, endExclusive, false), false);
    }
}
```
## 6. 정리
기본적으로 컨벤션은 사내 규칙을 따라서 일관성을 유지하는게 제일 좋습니다.

## 7. 더 알아볼 만한 주제
### 7.1 검증이 필요할 때
- verify: "어떤 절차를 제대로 수행했는지"물어보는 동사
    ```java
    verify(myObject).doSomething()
    ```
- validate: "사용자 입력이 제대로 되었는지"를 판단하는 경우
    ```java
    @Validated
    @RestController
    @RequiredArgsConstructor
    public class MyController { ... }
    ```
- check: 상태를 '확인'만 하는 경우
- is: is는 getter다.
### 7.2 코드 스타일
- 구글 코드 스타일 가이드
### 7.3 단어 조합은 3개 이하로

<hr>

# 객체 지향적인 코드 짜기 (1): 객체의 종류, 행동
## 1 객체의 종류
### 1.1 VO (Value Object)
모든 변수를 final로 선언해서 immutable한 객체를 VO(Value Object)라고 한다. VO를 만들 떄는 항상 값이 유효한지 체크해줘야 한다.
```java
class UserInfo {
    private final long id;
    private final String username;
    private final String email;

    public UserInfo(long id, String username, String email) {
        assert id > 0;
        assert StringUtils.isNotEmpty(username);
        assert EmailValidator.isValid(email);
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
```
VO는 불변해야 하며, 이는 동일하게 생성된 두 VO는 영원히 동일한 상태임을 유지되어야 한다는 것을 의미한다. 또한 VO는 잘못된 상태로는 만들어 질 수 없다. 따라서 인스턴스화 된 VO는 항상 유효하므로 버그를 줄이는데에도 유용하다.
### 1.2 DTO (Data Transfer Object)
DTO는 단순히 데이터 전달에 사용되는 객체를 의미한다. 메소드 간, 클래스 간, 프로세스 간에 데이터를 주고받을 때 사용한다. DTO는 상태를 보호하지 않으며 모든 속성을 노출하므로 획득자와 설정자가 필요 없다. 이는 public 속성으로 충분하다는 뜻이다.

### 1.3 Entity
Entity는 아래와 같은 조건을 만족해야 한다.
- 유일한 식별자
- 수명 주기
- 저장소에 저장
- 명명한 생성자와 명령 메서드를 사용해 인스턴스를 만들거나 그 상태를 조작하는 방법을 사용자에게 제공

### 1.4 생성자의 역할
생성자는 가급적 두개의 역할만 해야한다.
- 값을 검증
- 값을 할당

### 1.5 객체를 만들 때의 고민
객체의 종류에는 3종류만 있는 것이 아니며, 완벽한 분류도 어렵다.
- VO이면서 Entity 일 수 있다.
- DTO이면서 PO일 수 있다.
- 셋 다 아닐 수도 있다.

<br>
사실 분류보다 '어떤 값을 불변으로 만들 것인가?', '어떤 인터페이스를 노출할 것인가?'에 대한 고민이 더 중요하다.

## 2 디미터 법칙
#### 디미터 법칙 위반 코드
```java
class ComputerManager {
    public void printSpec(Computer computer) {
        long size = 0;
        for (int i = 0; i < computer.getDisks().size(); i++>) {
            size += computer.getDisks().get(i).getSize();   // Client에서 computer객체에 대해 내부 구현을 알고 있음
        }
        System.out.println(size);
    }
}
```
#### 디미터 법칙을 위반하지 않는 코드
```java
class ComputerManager {
    public void printSpec(Computer computer) {
        System.out.println(computer.getDiskSize());
    }
}
```
하지만 위 코드도 좋은코드는 아니다. 디스크 용량이 얼마인지 물어봐서 출력하지 말고. computer객체에 디스크 용량을 출력시키는 일을 시킨다. 
```java
class ComputerManager {
    public void main() {
        computer.printDiskSize();
    }
}
```

## 3 행동
Car라는 클래스를 만들때 데이터위주의 사고 보단 행동 위주의 사고를 하는것이 객체 지향적일 확률이 높다.
#### 데이터 위주의 사고
```java
class Car {

    private Frame frame;
    private Engine engine;
    private List<Wheel> wheels;
    private Direction direction;
    private Speed speed;
}
```
#### 행동 위주의 사고
```java
class Car {

    public void drive() {}
    public void changeDirection() {}
    public void accelerate(Speed speed) {}
    public void decelerate(Speed speed) {}
}
```
### 3.1 duck typing
- 행동이 같다면 같은 클래스로 부르겠다.
- "duck typing"이라는 용어는 덕 테스트에서 유래했다. 만약 어떤 새가 오리처럼 걷고, 헤엄치고, 꽥꽥거리는 소리를 낸다면 나는 그 새를 오리라고 부를 것이다.

## 4 순환 참조
순환 참조, 양방향 참조는 되도록 만들지 말자.
### 4.1 순환참조가 부자연 스러운 이유
- 순환 의존성 자체가 결합도를 높이는 원인이 된다.
- 순환참조 때문에 serialize가 불가능해 진다.
### 4.2 순환참조 해결 방법은 간접 참조로 해결한다
- Id로 필요할 때마다 찾아오는게 낫다.

#### 순환참조
```java
class User {

    private long id;
    private String username;
    private List<Feed> feeds;
}

class Feed {

    private long id;
    private String content;
    private User writer;

}
```
#### 간접 참조
```java
class User {

    private long id;
    private String username;
    private List<Feed> feeds;
}

class Feed {

    private long id;
    private String content;
    private long writerId;
}
```
## 5. 더 알아볼 만한 주제
### 5.1 항상 하면 좋은 고민
- final 이어야 할까?
- 이름은 뭘로하는게 좋을까?
### 5.2 VO의 변경자
- 새로운 VO를 반환한다.
- VO의 변경자 이름(eg. changePassword < withNewPassword>)
### 5.3 Immutable

<hr>

# 설계 (1) 의존성이란 무엇인지? (DI vs DIP)
## 1. SOLID
### 1.1 Single Response(단일 책임 원칙)
단일 책임 원칙(single responsibility principle)이란 모든 클래스는 하나의 책임만 가지며, 클래스는 그 책임을 완전히 캡슐화해야 함을 일컫는다. 클래스가 제공하는 모든 기능은 이 책임과 주의 깊게 부합해야 한다. 어떤 클래스나 모듈은 변경하려는 단 하나의 이유만을 가져야 한다고 결론 짓는다. 코드 라인이 100줄 이상이라면 의심해 봐야 한다.
### 1.2 Open Closed(개방-폐쇄 원칙)
개방-폐쇄 원칙(OCP, Open-Closed Principle)은 '소프트웨어 개체(클래스, 모듈, 함수 등등)는 확장에 대해 열려 있어야 하고, 수정에 대해서는 닫혀 있어야 한다'는 프로그래밍 원칙이다. 개방-폐쇄 원칙이 잘 적용되면, 기능을 추가하거나 변경해야 할 때 이미 제대로 동작하고 있던 원래 코드를 변경하지 않아도, 기존의 코드에 새로운 코드를 추가함으로써 기능의 추가나 변경이 가능하다. 이 원칙을 무시하고 프로그래밍을 한다면, 객체 지향 프로그래밍의 가장 큰 장점인 유연성, 재사용성, 유지보수성 등을 결코 얻을 수 없다.
### 1.3 Liskov substitution(리스코프 치환 원칙)
컴퓨터 프로그램에서 자료형 S가 자료형 T의 하위형이라면 필요한 프로그램의 속성(정확성, 수행하는 업무 등)의 변경 없이 자료형 T의 객체를 자료형 S의 객체로 교체(치환)할 수 있어야 한다는 원칙이다.
#### 상위 클래스와 하위 클래스 사이의 계약이 깨지는 경우
```java
@Getter
@Setter
@AllArgsConstructor
class Rectangle {

    protected long width;
    protected long height;
}

class Square extends Rectangle {
    public Square(long lenght) {
        super(lenght, lenght);
    }
}
```

```java
Rectangle square = new Sqare(10);
square.setHeight(5);
```
### 1.4 Interface-Segregation (인터페이스 분리 원칙)
인터페이스 분리 원칙은 클라이언트가 자신이 이용하지 않는 메서드에 의존하지 않아야 한다는 원칙이다. 인터페이스는 한마디로 "이 기능을 사용하고 싶다면 이 방법을 사용하세요."라고 알려주는 것이다. 결국 public 메서드가 인터페이스가 된다.

```java
class User {
    
    private String email;
    private String password;
    private boolean active;

    public void inactivate() {
        this.active = false;
    }

    public boolean equalsPassword(String plainPassword) {
        // do something
        String secretPassword = encode(plainPassword);
        return password.equals(secretPassword);
    }

    private String encode(String password) {
        return Encryptor.encode(password);
    }
}
```
위 User클래스를 보면 user를 비활성화시키는 inactivate메서드가 있고 비밀번호가 일치하는지 비교하는 equalsPassword메서드가 있는데 이 두개는 public으로 인터페이스다. 하지만 encode는 User를 사용하는 입장에서는 알 필요가 없기 때문에 private접근 제한자이고 인터페이스가 아니다.

<br>

결국 인터페이스 분리 원칙은 인터페이스를 적재적소에 잘 분리하라는 이야기 입니다.

### 1.5 Dependency Inversion(의존성 역전 원칙)
- 상위 모듈은 하위 모듈에 의존해서는 안된다. 상위 모듈과 하위 모듈 모두 추상화에 의존해야 한다.
- 추상화는 세부 사항에 의존해서는 안된다. 세부사항이 추상화에 의존해야 한다.
## 2. 의존성
생성자 의존성 주입이 7개 이상 넘어가거나 파라미터 의존성 주입이 4개 이상 넘어 간다면 클래스 분할이나 메서드 분할을 고려해봐야 한다는 신호이다.
## 3. 의존성 조언
### 3.1 의존성을 드러내라
내가 모르는 객체를 사용하다보면 어쩔 때는 동작하고 어쩔때는 동작을 안하는 경우가 있는데 이런경우 보통 내부에 감춰진 의존성이 있는 경우가 많다.

예를들어 사용자가 로그인하면 로그인 시간을 기록 하는 코드가 있다.
#### 의존성이 숨겨진 코드
```java
class User {
    private long lastLoginTimestamp;

    public void login() {
        this.lastLoginTimestamp = Clock.systemUTC().millis();
    }
}
```
내부 로직을 보면 login은 분명 Clock에 의존적이다.

```
user.login();
```
하지만 외부에서 보면 login이 시간에 의존하고 있음을 알 수가 없음. login이 제대로 작동하지 않을경우 계속 드릴 다운하면서 원인 분석을 해야하니 디버깅이 길어진다.

그리고 이런 경우 테스트하기가 난해해 진다. 로그인을 했을때 로그인 시각이 테스트할때의 시간과 같은지 테스트를 하려고하는데 당연히 로그인시점의 시간과 테스트시점의 시간이 다르다. 또한 로그인했을 당시의 호출 시간을 알 방법도 없다. 따라서 이 테스트는 만들기도 어렵고 일관되게 유지하기도 힘들다.
```java
class UserTest {

    @Test
    public void login_테스트() {
        // given
        User user = new User();

        // when
        user.login();

        // then
        assertThat(user.getLastLoginTimestamp()).isEqualTo(???);
    }
}
```
일반적으로 개발자들이 의존성을 실수로 숨기게 되는 흔한 케이스가 있습니다.
- 시간
- 랜덤 (Random)
이 두개의 경우는 실행할 때마다 변하는 값이다.

## 3.2 변하는 값은 주입 받아라
```java
class User {

    private long lastLoginTimestamp;

    public void login(Clock clock) {
        // ....
        this.lastLoginTimestamp = clock.millis();
    }
}
```
```java
user.login(Clock.systemUTC())
```
외부에서 보면 login은 분명히 시간이 필요한 메서드라는 것을 알 수 있고 테스트도 쉬워진다. 

```java
class UserTest {

    @Test
    public void login_테스트() {
        // given
        User user = new User();
        Clock clock = Clock.fixed(Instant.parse("2000-01-01T00:00:00.00Z"), ZoneId.of("UTC"));

        // when
        user.login(clock);

        // then
        assertThat(user.getLastLoginTimestamp()).isEqualTo(946684800000L);
    }
}
```
의존성이 제대로 풀려있지 않다면 테스트가 힘들기 때문에 테스트하기가 쉽다면 좋은 코드일 확률이 높다.

그렇지만 완전히 해결된 것은 아니다. login을 해야하는 부분에서 Clock이라는 숨겨진 의존성을 사용해야 하기 때문이다. 같은 문제각 발생한 것이다.

```java
class UserService {

    public void login(User user) {
        // ...
        user.login(Clock.systemUTC());
    }
}
```
user.login은 Clock을 의존한다는 것은 알겠는데 UserService의 login메소드는 Clock을 또 여전히 감추고 있다.

```java
class UserServiceTest {
    @Test
    public void login_테스트() {
        // given
        User user = new User();
        UserService userService = new UserService();

        // when
        userService.login(user);

        // then;
        assertThat(user.getLastLoginTimestamp()).isEqualTo(???);
    }
}
```
마찬가지로 UserService는 테스트하기 어렵다는 문제를 가지고 있다. 이런식으로 결국엔 폭탄돌리기를 하고있는 것이다. 결국엔 누군가는 고정된 의존성을 사용해야 되고 이걸 주입해 줘야 되기 때문이다. 그래도 다행인점은 의존성을 제대로 처리하지 않으면, 테스트하기 힘들다는 결론을 알 수 있다.

### 3.3 변하는 값을 추상화시켜라
결론적으로 변하는 값에대한 가장 괜찮은 접근법은 런타임 의존성과 컴파일 타임 의존성을 다르게 하는 것이다.

```java
interface ClockHolder {

    long getMillis();
}

@Getter
class User {

    private long lastLoginTimestamp;
 
    public void login(ClockHolder clockHolder) {
        // ...
        this.lastLoginTimestamp = clockHolder.getMillis();
    }
}

@RequiredArgsConstructor
class UserService {

    private final ClockHolder clockHolder;

    public void login(User user) {
        // ...
        user.login(clockHolder.getMillis());
    }
}
```
만약 ClockHolder라는 인터페이스를 만들고 현재시간을 알려주는 getMillis메서드를 정의했다고 가정하자. User는 Clock에 의존하는게 아니라 ClockHolder를 의존하게 바뀌었고 UserService에서 ClockHolder는 UserService의 멤버 변수로 들어갔다. 이렇게 되면 User는 어떤 ClockHolder가 올지 모르지만 알필요 없고 컴파일 시 ClockHolder에만 의존하면 된다.

결국 실제 배포환경과 테스트환경에서 사용할 구현체를 각각 만들어서 런타임 마다 다른 구현체를 사용하도록 할 수 있다.
#### 프로덕션용
```java
class SystemClockHolder implements ClockHolder {

    @Override
    public long getMillis() {
        return Clock.systemUTC().millis();
    }
}
```

#### 테스트용
```java
class TextClockHolder implements ClockHolder {

    private Clock clock;

    @Override
    public long getMillis() {
        return clock.millis();
    }
}
```

이 테스트 코드는 쉽게 깨지지 않고 항상 같은 결과를 주는 테스트 코드가 된다.
```java
class UserServiceTest {
    @Test
    public void login_테스트() {
        // given
        Clock clock = Clock.fixed(Instant.parse("2000-01-01T00:00:00.00Z"), ZoneId.of("UTC"));
        User user = new User();
        UserService userService = new UserService(new TestClockHolder(clock));

        // when
        userService.login(user);

        // then;
        assertThat(user.getLastLoginTimestamp()).isEqualTo(9466840000L);
    }
}
```
지금 까지 한 행위는 <b>의존성 역전 원리</b>를 이용하여 <b>컴파일 타임과 런타임</b>의 의존성을 다르게 했다. 이렇게 의존성을 추상화 시키는 방식은 매우 중요한 기법이다!.
## 4. CQRS
Command and Query Responsibility Segregation의 약자로 명령과 질의를 분리하라는 뜻인데 일단 명령이랑 질의가 뭔지부터 확인해보자. 
- Command(명령): 명령은 쉽게 말해서 일을 시키는 메서드를 의미한다. 명령 메서드는 객체의 상태를 변경시킨다는 특징이 있고 return값을 가지지 않는다.
- Query(질의): 질의는 쉽게 말해서 상태만 물어보는 메서드다. 그래서 질의 메서드는 객체의 상태를 변화시켜선 안된다.
다시 말해 CQRS는 명령과 질의를 철저히 분리시키는 이론이라 보면 된다.
> 하나의 메소드는 명령이나 쿼리여야하며, 두 가지 기능을 모두 가져서는 안된다. 명령은 객체의 상태를 변경할 수 있지만, 값을 반환하지 않는다. 쿼리는 값을 반환하지만 객체를 변경하지 않는다.
## 5. 더 알아볼 만한 주제
### 5.1 정답이 없다.
- Shotgun surgery: 기능 산재 - 모아둬야 할 것을 분할해서 발생
- Divergent change: 수정 산발 - 분할해야 할 것을 모아놔서 발생
### 5.2 리팩토링
working effectively with legacy code책 추천
### 5.3 다양한 설계 조언
- Cargo cult programming: 이해는 하지 않고 그냥 무작정 따라서 프로그래밍하는 것.
- DRY: Don't Repeat Yourself: 똑같은 일을 두 번 하지 마라.
- KISS: Keep it simple, stupid: 단순하게 하라.
- YAGNI: You Ain't Gonna Need It: 필요할 때 해라.
- DAMP: Descriptive And Meaningful Phrases: 서술적이고 의미 있으며 구어적으로 작성해라. (테스트 코드 한정)

<hr>

# 기타 팁
## 1. 오해
### 1.1 500 Response
500에러는 사실상 장애이다. API가 실패한다면 원인을 반드시 알려줘야 하고 이에 대응하는 error code를 내려줘야 한다.
### 1.2 개발의 목적
#### 좋은 프로그램
1. <b>돌아가야 한다</b>.
2. 유지 보수가 가능해야 한다.
### 1.3 디자인 패턴에 매몰되지 않는다.
디자인 패턴을 위한 시스템이 되선 안된다. 패턴은 도구일 뿐 개발의 목적을 잊지 말자.
### 1.4 프로그래머 vs 소프트웨어 엔지니어
프로그래밍과 소프트웨어 엔지니어링의 차이는 시간, 규모, 트레이드 오프를 고려할 줄 아는 사람인지 아닌지에 의해 차이가 있습니다.
## 2. OOP
### 2.1 OOP는 '객체'지향 프로그래밍이다.
클래스라는 건 어디까지나 객체를 잘 다루기 위한 도구이고 개발자가 집중해야하는 것은 객체이다.
### 2.2 TDD / DDD / FP
이 모든이론이 가리키는 방향이 결국 잘 설계된 OOP이다. 그렇기 때문에 이거 다 조급하게 공부할 필요없고 천천히 학습하자.
### 2.3 DDD (Domain Driven Design)
### 2.4 vs 절차지향
OOP 가 항상 정답은 아니다. 
### 2.5 역할극
OOP 에서 제일 중요한 것은 역할, 책임, 협력이다.
### 2.6 객체지향 생활 체조 9가지 원칙
1. 한 메서드에 오직 한 단계의 들여쓰기(indent)만 한다.
2. else 예약어를 쓰지 않는다.
3. 모든 원시값과 문자열을 포장한다.
4. 한 줄에 점을 하나만 찍는다.
5. 줄여쓰지 않는다(축약 금지).
6. 모든 엔티티를 작게 유지한다.
7. 3개 이상의 인스턴스 변수를 가진 클래스를 쓰지 않는다.
8. 일급 컬렉션을 쓴다.
9. getter/setter/property를 쓰지 않는다.
## 3. 습관
### 3.1 apache utils
CollectionUtils / StringUtils / ObjectUtils ... 이 Utils들을 사용하면 좋다. null체크를 wrapping해서 해주기 때문에 편리하다. 단 라이브러리를 사용하는 경우 보안 패치를 꼼꼼히 따라가 줘야하고 라이센스도 확인해야 한다 .
```java
if(name == null || name.isEmpty()) {
    return true;
}

if(StringUtils.isEmpty(name))
    return true;
```
### 3.2 상속
상속을 지양하고 Composition을 지향해야 한다. 컴포지션이란 간단히 말해서 공통 기능을 별도의 컴포넌트로 분리하고 그 컴포넌트를 멤버 변수로 갖고 있어서 기능을 이용하게 하는게 컴포지션 이다.
### 3.3 테스트
테스트를 먼저 생각 해본다. 테스트하기 쉬운 코드가 좋은 설계일 확률이 높다. 만약 TDD를 적용할 수 없는 환경이라면 이런 생각을 하면서 개발하는 것만으로도 큰 도움이 된다.
### 3.4 블락
블락이 생긴다면 메소드 분할을 고려해본다. 메서드에 코드가 블락이 여러개 생기면 각 블락은 '이건 논리적으로 다른 코드야'라고 선언하는 것이다. 따라서 이런 코드는 적절한 메서드명을 선언해서 분할 해 준다.
### 3.5 Tab in tab in tab...
들여쓰기가 2개 이상 들어가지 않도록 한다. 만약 들여쓰기가 과도하게 들어간다면 메서드로 분할한다.
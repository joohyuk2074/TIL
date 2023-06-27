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
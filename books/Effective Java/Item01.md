## 정적 팩터리 메서드의 장점 1

보통 어떤 클래스의 인스턴스를 생성할 때 아래와 같이

```java
public class Order {

    private boolean prime;

    private boolean urgent;

    private Product product;

    private OrderStatus orderStatus;

    public Order(Product product, boolean prime) {
        this.product = product;
        this.prime = prime;
    }
}

```
생성자를 통해서 해당 인스턴스를 생성하게 됩니다.

하지만 만약에 생성자를 통해서 product, prime의 값이 아니라 product, urgent를 생성자의 매개변수로 하는 인스턴스도 만들고 싶을 때 생성자를 추가 하게 되면

```java
public Order(Product product, boolean prime) {
    this.product = product;
    this.prime = prime;
}
    
public Order(Product product, boolean urgent) {
    this.product = product;
    this.urgent = urgent;
}
```
jvm은 생성자의 파라미터 명이 아닌 파라미터의 타입을 통해서 생성자를 식별하기 때문에 위와 같이 파라미터의 타입이 Product, boolean으로 시그니처가 같은 생성자가 2개가 존재할 수 없습니다. 생성자는 오직 한 종류의 시그니처만 존재해야 합니다.

그래서 이런 시그니처 중복을 피하기 위해서 Product, Boolean 파라미터의 순서를 변경하여
```
public Order(Product product, boolean prime) {
    this.product = product;
    this.prime = prime;
}
    
public Order(boolean urgent, Product product) {
    this.product = product;
    this.urgent = urgent;
}
```
이런식으로 아래 생성자의 파라미터의 순서를 변경하면 다른 시그니처로 인식하기때문에 가능하긴합니다. 하지만 이렇게 만들게 되면 생성자의 이름을 표현할 수 없습니다.

이렇게 시그니처는 같지만 다른 필드를 초기화 하고 싶을때 정적 팩터리 메서드를 고려할 수 있습니다.

```java
public class Order {

    private boolean prime;

    private boolean urgent;

    private Product product;

    private OrderStatus orderStatus;

    public static Order primeOrder(Product product) {
        Order order = new Order();
        order.prime = true;
        order.product = product;

        return order;
    }

    public static Order urgentOrder(Product product) {
        Order order = new Order();
        order.urgent = true;
        order.product = product;
        return order;
    }

```
이런식으로 정적팩터리 메서드의 첫번째 장점은 메서드명에 구체적으로 어떤 인스턴스를 생성할건지 구체적으로 명시가 가능하다는 장점이 있습니다.

따라서 생성자의 시그니처가 중복되는 경우에 정적팩터리 메서드를 고려할 수 있습니다.

## 정적 팩터리 메서드의 장점 2

```java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

}

```
위의 클래스의 인스턴스를 생성하려면 매번 new Settings()를 통해서 매번 새로운 인스턴스를 생성해야 합니다. 하지만 어떤 경우는 특정한 경우에만 생성자를 생성할 수 있게 통제해야하는 경우가 있습니다. 이런경우에도 정적 팩터리 메서드를 고려해볼 수 있습니다.

```java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

    private Settings() {}	// 기본 생성자의 접근제한을 private으로 변경

    private static final Settings SETTINGS = new Settings();

    public static Settings getInstance() {
        return SETTINGS;
    }

}
```
따라서 이런식으로 외부에서 인스턴스를 마음대로 생성하지 못하게 기본생성자의 접근제한을 private으로 변경 후에 Setting 인스턴스를 미리 생성해놓고 정적 팩터리 메서드로 생성된 getInstance메서드를 통해서만 객체를 생성할 수 있습니다.

생성자를 public하게 제공하는 순간부터 인스턴스 생성을 컨트롤 할 수없습니다. 외부에서 new Settings()를 통해서 마음대로 새로운 인스턴스를 생성할 수 있기 때문입니다. 하지만 생성자를 private으로 변경하여 정적 팩터리 메서드를 이용하여 생성하면 인스턴스의 생성을 자기자신이 컨트롤 하겠다는 의미가 됩니다.


## 정적 팩터리 메서드의 장점 3, 4, 5
정적 팩터리 메서드의 세번째 장점은 아래의 코드처럼 인터페이스 타입을 사용할 수 있다.
```java
public class HelloServiceFactory {
	public static HelloService of(String lang) {
    	if(lang.equals("ko")){
        	return new KoreanHelloService();
        } else {
        	return new EnglishHelloService();
        }
    }
}
```
리턴타입은 HelloService 인터페이스이지만 실제 인스턴스는 HelloService의 구현체로 인스턴스를 생성할 수 있습니다. 또한 리턴타입을 클래스로 하고 리턴타입의 하위 클래스를 생성하여 리턴할 수도 있고 파라미터 값에 따라서 HelloService의 구현체를 각각 생성할 수도 있고 상당히 유연하게 인스턴스를 생성할 수 있습니다.

```java
HelloService ko = HelloServiceFactory.of("ko");
```
이렇게 인스턴스를 생성하면 클라이언트코드로부터 구체적인 타입을 숨겨 인터페이스 기반의 프레임워크를 사용하도록 강제할 수 있습니다. 

자바 8이전에는 인터페이스에 정적 팩터리 메서드를 선언할 수 없었지만 java 8이후엔 인터페이스에도 정적 팩터리 메서드를 선언할 수 있게 가능해졌습니다. 따라서 HelloServiceFactory클래스에 따로 메서드를 선언할 필요 없이 인터페이스에 정적 팩터리 메서드를 추가하면 됩니다.

```java
public class HelloService {

	String hello();
    
	static HelloService of(String lang) {
    	if(lang.equals("ko")){
        	return new KoreanHelloService();
        } else {
        	return new EnglishHelloService();
        }
    }
}
```

```java
HelloService ko = HelloService.of("ko);
```
따라서 HelloService인터페이스를 통해서 인스턴스를 생성할 수 있습니다.

그런데 책에서 다섯 번째 장점으로 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다고 하는데 책으로만 보면 이해가 잘 가지 않았습니다. 다행히 강의에서 이부분에 대한 설명을 자세하게 해 주셨습니다.
 
```java
public interface HelloService {
	
    String hello();
}
```
이렇게 인터페이스만 있고 구현체가 없는 상태입니다.

```java
public class HelloServiceFactory {
	
    public static void main(String[] args) {
    	ServiceLoader<HelloService> loader = ServiceLoader.load(HelloService.class);  // 1.
        Optional<HelloService> helloServiceOptional = loader.findFirst(); // 2.
        helloServiceOptional.ifPresent(h -> {		// 3.
        	System.out.println(h.hello());
        });
        
    }
}
```
ServiceLoader라는 자바가 기본으로 제공해주는 정적 패터리 메서드가 있습니다. 
1: 이렇게 load메서드의 인자로 HelloService.class 인터페이스를 전달하면 지금 현재 참조할 수 있는 classpath내에 있는 등록된 모든 HelloService의 구현체를 가져옵니다.
2: HelloService중에 첫번째 구현체를 찾습니다. 없을수도 있기 때문에 Optional로 받아옵니다.
3: HelloService의 구현체가 있다면 그 구현체의 hello메서드의 값을 출력합니다.

이렇게 되면 결과로 Ni Hao가 호출되는 것을 볼 수 있습니다.
```
Ni Hao
```

HelloService인터페이스만 있는데 어떻게 ChinenseHelloService가 동작하는지 의아 합니다. 그 이유는 해당 프로젝트가 의존하는 jar에 HelloService의 구현체인 ChineseHelloService가 구현체로 등록이 되어있기때문에 해당 구현체를 load해서 Ni Hao를 출력할 수 있게 되는것입니다.


그렇다면 아래 코드처럼 직접 ChineseHelloService를 구현해서 사용하면 되지 왜 이렇게 복잡하게 하느냐고 의문이 들 수 있습니다.

```java
public class HelloServiceFactory {
	
    public static void main(String[] args) {
        HelloService helloService = new ChineseHelloService();
        System.out.println(helloService.hello());
    }
}
```

차이점은 맨처음 ServiceLoader를 통해서 구현체를 가져온경우는 ChineseHelloService를 직접적으로 의존하지 않고 HelloSerivce의 구현체가 온다는 정보밖에 없습니다. 하지만 바로 위코드는 명시적으로 ChineseHelloService를 생성하기때문에 의존적입니다.

회사 프로젝트를 진행할 때 이런경우는 없지만 종종 필요할때가 있다고 합니다. 어떤 구현체가 올지는 모르지만 그 구현체가 의존하는 인터페이스기반으로 코딩을 할때 정적 팩터리 메서드를 고려할 수 있습니다. 하지만 책에서 예시를 잘 들어놨습니다.

JDBC driver 가 대표적인데 어떠한 데이터베이스를 사용할지는 모르지만 Connection의 구현체를 가져와서 사용할 수 있도록 합니다.

## 정적 팩터리 메서드의 단점 1, 2

```java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

    private Settings() {}	// 기본 생성자의 접근제한을 private으로 변경

    private static final Settings SETTINGS = new Settings();

    public static Settings getInstance() {
        return SETTINGS;
    }

}
```
첫번째 단점은 정적 팩터리 메서드만 사용할 수 있게 만들려면 생성자의 접근을 private으로 설정해야합니다. 이 말은 Settings클래스는 상속을 허용하지 않게 된다는 것입니다. 

```java
public class ChildSettings {
	
    private Settings settings;
}
```
하지만 위와 같이 Delegation으로 Settings를 클래스의 필드로 Settings의 기능을 상속받은 것 처럼 쓸 수 있기때문에 단점이라고 보기도 애매할 수 있습니다.

또한 정적 팩터리메서드와 생성자를 같이 쓸수 있는 경우도 있을 수 있습니다. 예를 들어 리스트가 그렇습니다. 
```java
List<String> list = new ArrayList<>();
List.of();
```
이렇게 생성자로도 리스트를 생성할 수 있지만 정적 팩터리 메서드인 of를 통해서도 리스트를 생성할 수 있습니다.

두번째 단점은 문서화와 관련이 있는데 
```java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

    public Settings() {}	// 기본 생성자
    
    public Settings(boolean useAutoSteering, boolean useABS, Difficulty difficulty) {
    	this.useAutoSteering = useAutoSterring;
        this.useABS = useABS;
        this.difficulty = difficulty;
    }

    private static final Settings SETTINGS = new Settings();

    public static Settings getInstance() {
        return SETTINGS;
    }

}
```
이런 식으로 생성자와 정적팩터리 메서드가 많아진다면 javadoc에서 인스턴스를 생성해 주는 용도의 메서드를 찾기가 어려워집니다. 정적팩터리 메서드만 지원한다면 Constructor Summary부분이 없기 때문에 javadoc을 읽는 사람들은 인스턴스를 어떻게 생성하는지 바로 파악하기가 힘들어 집니다. 그래서 네이밍 패턴을 정해서 사용하기를 권장하고 있습니다.

Namaing Convention
- from: 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드
  ```java
  Date d = Date.from(instant);
  ```

- of: 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
  ```java
  Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);
  ```
  
- valueOf: from과 of의 더 자세한 버전
  ```java
  BigInteger prime = BingInteger.valueOf(Integer.MAX_VALUE);
  ```

- instance 혹은 getInstance: (매개변수를 받는다면) 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스임을 보장하지는 않는다.
  ```java
  StackWalker luke = StackWalker.getInstance(options);
  ```

- create 혹은 newInstance: instance 혹은 getInstance와 같지만, 매번 새로운 인스턴스를 생성해 반환임을 보장한다.
  ```java
  Object newArray = Array.newInstance(classObject, arrayLen);
  ```
  
- getType: getInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 쓴다. "Type"은 팩터리 메서드가 반환할 객체의 타입이다.

  ```java
  FileStore fs = Files.getFileStore(path);
  ```
  
- newType: newInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 쓴다. "Type"은 팩터리 메서드가 반환할 객체의 타입이다.
  ```java
  BufferedReader br = Files.newBufferedReader(path);
  ```

- type: getType과 newType의 간결한 버전
  ```java
  List<Complaint> litany = Collections.list(legacyLitany0;
  ```
  
### 심화학습 자료
- https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html
- https://docs.oracle.com/javase/tutorial/ext/basics/spi.html#introduction
  
### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#
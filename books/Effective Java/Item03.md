프로젝트를 진행하다보면 애플리케이션에서 여러 인스턴스가 필요하지 않은 경우가 종종 있습니다. 예를 들어서 게임의 설정같은 경우 게임의 밝기, 게임상에서 사용하는 언어의 종류와 같은 인스턴스는 하나만 있어도 됩니다. 이러한 경우에 싱글턴을 어떻게 보증해야할지 알아봅시다.

## 1. public static final 필드 방식의 싱글턴
```java
public class Elvis implements IElvis, Serializable {

    /**
     * 싱글톤 오브젝트
     */
    public static final Elvis INSTANCE = new Elvis();

    private Elvis() { ... }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }
}
```
Elivis의 생성자를 private으로 만들면 외부에서는 Elvis생성자를 호출할 수 없게되고 public static final로 INSTANCE라는 상수에 미리 인스턴스를 생성합니다. 

이렇게되면 해당 클래스를 사용하는 클라이언트는 미리 생성된 인스턴스를 가져온다음에 leaveTheBuilding이라는 인스턴스 메서드를 호출할 수 있습니다.

### 1.1 장점

```java
Elvis elvis = Elvis.INSTANCE;
elvis.leaveTheBulding();
```

public static final 필드 방식의 장점은 코드가 보기 좋고 javadoc을 생성했을 때 최상단의 Fileds 섹션에 INSTANCE애 대한 설명이 위치해 있어 명시적으로 싱글톤임을 파악하기 쉽습니다.

### 1.2 단점1
EffectiveJava에서는 클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트를 테스트하기가 어려워질 수 있다고 하는데 이 설명만으로는 확 와닫지 않았습니다. 그래서 이 설명에 대해 좀 이해해봅시다. 

```java
public class Concert {

    private boolean lightsOn;

    private boolean mainStateOpen;

    private Elvis elvis;

    public Concert(Elvis elvis) {
        this.elvis = elvis;
    }

    public void perform() {
        mainStateOpen = true;
        lightsOn = true;
        elvis.sing();
    }

    public boolean isLightsOn() {
        return lightsOn;
    }

    public boolean isMainStateOpen() {
        return mainStateOpen;
    }
}
```
위의 Concert 클래스는 Elivs를 의존하는 클라이언트 코드입니다. 코드를 살펴보면 perform메서드 블록안에 elvis인스턴스에 sing메서드를 호출하는 부분이 있습니다. 우리는 perform메서드를 호출할때 mainStateOpen과 lightsOn이 true가 되는지 테스트를 하고자합니다. 
<br>


```java
class ConcertTest {

    @Test
    void perform() {
        Concert concert = new Concert(Elvis.INSTANCE);
        concert.perform();

        assertTrue(concert.isLightsOn());
        assertTrue(concert.isMainStateOpen());
    }

}
```
이런식으로 실제 Elvis의 인스턴스를 받아서 테스트할 수는 있습니다. 그런데 만약 sing메서드가 다른 외부의 api와 호출을 하거나 실행시간이 상당히 오래걸리는 메서드라고 가정해봅시다. 그렇게되면 매번 테스트를 실행할 때마다 elvis객체의 sing메서드를 호출하기는 상당히 부담스럽고 심지어 이 테스트의 핵심도 아닙니다. 이러면 실제 elvis인스턴스의 sing메서드가 실행되기 때문에 굉장히 비효율적인 테스트코드라는 것을 알 수 있습니다.

만약 Elvis를 인터페이스의 구현체라고 하고 테스트 코드를 생성해 보겠습니다.
```java
public interface IElvis {

    void leaveTheBuilding();

    void sing();
}
```
```java
public class Elvis implements IElvis {

    /**
     * 싱글톤 오브젝트
     */
    public static final Elvis INSTANCE = new Elvis();
    private static boolean created;

    private Elvis() {
        if (created) {
            throw new UnsupportedOperationException("can't be created by constructor.");
        }

        created = true;
    }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

    public void sing() {
        System.out.println("I'll have a blue~ Christmas without you~");
    }

    private Object readResolve() {
        return INSTANCE;
    }

}

```
IElvis라는 인터페이스를 생성하고 IElvis의 구현체로 Elvis를 생성한다면

```java
public class Concert {

    private boolean lightsOn;

    private boolean mainStateOpen;

    private IElvis elvis;		// 인터페이스 타입으로 선언

    public Concert(IElvis elvis) {  // 인터페이스 타입으로 선언
        this.elvis = elvis;
    }

    public void perform() {
        mainStateOpen = true;
        lightsOn = true;
        elvis.sing();
    }

    public boolean isLightsOn() {
        return lightsOn;
    }

    public boolean isMainStateOpen() {
        return mainStateOpen;
    }
}
```
매개변수를 인터페이스 타입으로 선언해서 IElvis의 구현체를 받을 수 있습니다. 이렇게 인터페이스기반으로 구현을하면 가짜 Elvis객체를 생성자로 넘겨서 테스트 할 수 있습니다.

```java
public class MockElvis implements IElvis {
    @Override
    public void leaveTheBuilding() {

    }

    @Override
    public void sing() {
        System.out.println("You ain't nothin' but a hound dog.");
    }
}
```

```java
class ConcertTest {

    @Test
    void perform() {
        Concert concert = new Concert(new MockElvis());
        concert.perform();

        assertTrue(concert.isLightsOn());
        assertTrue(concert.isMainStateOpen());
    }

}
```
이렇게 Mocking한 Elvis객체를 받아서 테스트를 하면 perfom메서드를 실행할때 elvis객체의 sing메서드가 작동하는지 테스트 하는게 아니라 mainStateOpen과 lightsOn이 true가 되는지 테스트하는게 목적이기 때문에 sing메서드를 금방 실행시킬 수 있어 효율적인 테스트가 가능합니다. 책에서 말하는 테스트가 어렵다는 부분이 이렇게 인터페이스로 가짜구현체를 생성해서 테스트를 할 수 없기때문에 테스트하기가 어렵다는 뜻이었습니다.

### 1.3 단점2
리플렉션을 사용하면 싱글톤이 깨지게 됩니다. 

```java
public class ElvisReflection {

    public static void main(String[] args) {
        try {
            Constructor<Elvis> defaultConstructor = Elvis.class.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            Elvis elvis1 = defaultConstructor.newInstance();
            Elvis elvis2 = defaultConstructor.newInstance();
            Elvis.INSTANCE.sing();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
```
리플렉션을 통해서 private으로 접근제한된 기본생성자를 접근하고 있습니다. getDeclaredConstructor메서드는 접근지시자에 상관없이 선언된 생성자에 접근할 수 있습니다. 그리고 setAccessible에 true값을 줘야 private한 생성자에 접근할 수있습니다.

따라서 이런경우는 생성자가 2번이상 호출할때 새로운 인스턴스 객체를 막도록 책에서 권장합니다.

```java
public class Elvis implements IElvis, Serializable {

    /**
     * 싱글톤 오브젝트
     */
    public static final Elvis INSTANCE = new Elvis();
    private static boolean created;

    private Elvis() {
        if (created) {
            throw new UnsupportedOperationException("can't be created by constructor.");
        }

        created = true;
    }

	// ...

}
```
인스턴스가 생성되었는지 확인하는 created 플래그를 필드에 선언하고 private 기본 생성자에 create이 true일 경우 UnsupportedOperationException을 던지고 false일 경우 true로 변경하도록 하면 리플렉션으로 기본생성자에 접근하게되면 예외가 발생해 새로운 인스턴스를 생성할 수없습니다.

이와 비슷한 문제로 역직렬화를 할 때 새로운인스턴스가 생길수 있습니다. 

```java
public class ElvisSerialization {

    public static void main(String[] args) {
    	// 직렬화 해서 저장하는코드
        try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream("elvis.obj"))) {
            out.writeObject(Elvis.INSTANCE);
        } catch (IOException e) {
            e.printStackTrace();
        }

		// 역직렬화로 읽어오는 코드
        try (ObjectInput in = new ObjectInputStream(new FileInputStream("elvis.obj"))) {
            Elvis elvis3 = (Elvis) in.readObject();
            System.out.println(elvis3 == Elvis.INSTANCE);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
```
직렬화를 할때 인스턴스가 처음 생성되었고 역직렬화로 객체를 읽어올때 인스턴스가 한번 더 생성된다는 것입니다. 이 코드를 실행하면 elvis3와 Elvis.INSTANCE의 값이 달라 false로 나옵니다.

```java
public class Elvis implements IElvis, Serializable {

    /**
     * 싱글톤 오브젝트
     */
    public static final Elvis INSTANCE = new Elvis();
    private static boolean created;

    private Elvis() {
        if (created) {
            throw new UnsupportedOperationException("can't be created by constructor.");
        }

        created = true;
    }

	// ......

	// 역직렬화시 사용되는 메서드
    private Object readResolve() {
        return INSTANCE;
    }

}
```
역직렬화를 할 때 호출되는 메서드가 있는데 이 메서드는 오버라이딩과는 좀 다른부분이 있습니다. 어떠한 인터페이스도 없이 readResolve라는 메서드를 호출하게되는데 오버라이딩 비슷하게 readResolve메서드를 생성해서 기존에 생성된 인스턴스를 반환하게 하면 역직렬화시 readResolve를 호출해 새로운 인스턴스를 생성하지 않습니다.

```java
public class ElvisSerialization {

    public static void main(String[] args) {
    	// 직렬화 해서 저장하는코드
        try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream("elvis.obj"))) {
            out.writeObject(Elvis.INSTANCE);
        } catch (IOException e) {
            e.printStackTrace();
        }

		// 역직렬화로 읽어오는 코드
        try (ObjectInput in = new ObjectInputStream(new FileInputStream("elvis.obj"))) {
            Elvis elvis3 = (Elvis) in.readObject();
            System.out.println(elvis3 == Elvis.INSTANCE);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
```
그래서 위의 코드를 실행하면 이번엔 true가 나옵니다.

Spring을 쓴다면 굳이 이렇게 복잡하게 설정할 필요없이 스프링빈으로 등록하면 기본적으로 싱글톤을 보장하게 됩니다.

## 2. public static 메서드 방식의 싱글턴
```java
public class Elvis implements Singer {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() { }
    public static Elvis getInstance() { return INSTANCE; }

	// ...
}
```
public static final field방식의 싱글턴과 다른점은 INSTANCE필드의 접근제한을 public -> private으로 변경하고 public static 메서드인 getInstance를 통해서 Elvis 싱글턴객체를 반환하도록 한다는 것입니다.

이 방식의 단점도 생성자를 사용하기 때문에 public stataic final field방식의 단점과 같습니다.

### 2-1 장점1
public static final field방식과의 다른 장점은 클라이언트의 코드가 바뀌지않고 동작을 바꿀수 있게 됩니다.

```java
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() { }
    
   
    public static Elvis getInstance() { 
    	return new Elvis();
    }

	// ...
}
```
public static final field방식은 무조건 같은 인스턴스를 반환하지만 getInstance() 메서드 방식을 이용하면 싱글턴이든 새로운 인스턴스를 생성하든 메서드안에서만 변경하고 클라이언트쪽에 코드변경이 원하는대로 동작을 변경할 수 있습니다.

### 2-2 장점2
책에서는 원한다면 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다는 점이라고 하는데 책에서는 이렇게 한문장으로만 설명이 되어있어 코드로 좀더 살펴보겠습니다.

```java
public class MetaElvis<T> {

    private static final MetaElvis<Object> INSTANCE = new MetaElvis<>();

    private MetaElvis() { }

    @SuppressWarnings("unchecked")
    public static <E> MetaElvis<E> getInstance() { 
    	return (MetaElvis<E>) INSTANCE; 
    }

	public void say(T t) {
        System.out.println(t);
    }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

}
```
제네릭한 타입으로 동일한 인스턴스객체를 사용하고 싶을때 아래와 같이 제네릭 싱글톤 팩터리를 만들어 사용할 수 있다는 뜻입니다.

```java
 public static void main(String[] args) {
    MetaElvis<String> elvis1 = MetaElvis.getInstance();
    MetaElvis<Integer> elvis2 = MetaElvis.getInstance();
    System.out.println(elvis1);
    System.out.println(elvis2);
    elvis1.say("hello");
    elvis2.say(100);
}
```
```
me.whiteship.chapter01.item03.staticfactory.MetaElvis@7229724f
me.whiteship.chapter01.item03.staticfactory.MetaElvis@7229724f
hello
100
```
결과를 보면 인스턴스는 같고 원하는 타입을 바꿔서 쓸 수 있다는 것을 알 수 있습니다.


### 2-3 장점3
책에서 세번째 장점으로 정적 팩터리의 메서드 참조를 공급자로 사용할 수 있다고 하는데 이것도 문장으로만 설명이 되어있어서 코드를 통해서 좀더 알아보겠습니다.

```java
public class Concert {

    public void start(Supplier<Singer> singerSupplier) {
        Singer singer = singerSupplier.get();
        singer.sing();
    }
}
```
Conceert 클래스에서 start메서드의 파라미터 타입인 Supplier는 자바8에 들어가 Functional인터페이스를 뜻합니다.

```java
@FunctionalInterface
public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}
```
자바8에는 이렇게 @FunctionalInterface 어노테이션이 붙어있는 기본적인 Function들을 제공합니다. 더 자세한 내용은 https://www.baeldung.com/java-8-functional-interfaces 이부분을 참고하시면 도움이 될거 같습니다.



```java
public interface Singer {

    void sing();
}
```
```java
public class Elvis implements Singer {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() { }
    public static Elvis getInstance() { return INSTANCE; }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

    @Override
    public void sing() {
        System.out.println("my way~~~");
    }
}

```
함수형 인터페이스인 Singer를 선언하고 그 Singer를 구현하는 Elvis의 sing메서드를 실행할떄 "my way~~~"가 출력되도록 오버라이딩을 하도록 설정합니다.

```java
public class Concert {

    public void start(Supplier<Singer> singerSupplier) {
        Singer singer = singerSupplier.get();
        singer.sing();
    }

    public static void main(String[] args) {
        Concert concert = new Concert();
        concert.start(Elvis::getInstance);
    }
}
```
그리고 위의 main 메서드를 실행하게 되면 오버라이드된 "my way~~~"가 출력되게 됩니다.
결론적으로 Elvis::getInstance이렇게 메서드참조를 통해 supplier형식에 맞춰 공급자로 쓰일 수 있습니다.

## 3. 열거타입 방식의 싱글턴
```java
public enum Elvis {
    INSTANCE;

    public void leaveTheBuilding() {
        System.out.println("기다려 자기야, 지금 나갈께!");
    }
}

```
Enum타입으로 Elvis를 선언하였습니다. private을 생성자로 만들필요도 없고, public static메서드를 만들필요도 없고 필드를 정의할 필요도 없습니다. 그리고 우리가 원하는 인스턴스 메서드를 선언하기만 하면 됩니다.

```java
public static void main(String[] args) {
    Elvis elvis = Elvis.INSTANCE;
    elvis.leaveTheBuilding();
}
```
이런식으로 사용할 수 있습니다. 이런방법을 권장하는 이유는 직렬화, 역직렬화와 리플렉션에 안전한 방법이기 때문입니다.

### 심화학습자료
- 언체크예외 https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html
- 메서드참조 https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
- https://blogs.oracle.com/javamagazine/post/understanding-java-method-invocation-with-invokedynamic
- https://docs.oracle.com/javase/8/docs/platform/serialization/spec/serialTOC.html

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#
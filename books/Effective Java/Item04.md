클래스를 구현하다보면 인스턴스를 생성할 필요가 없는 경우가 가끔 있습니다. 

```java
public class UtilityClass {

    public static String hello() {
        return "hello";
    }
}
```
위와 같이 정적 메서드만 담은 정적 유틸리티 클래스가 그런경우인데 보통 정적 유틸리티 클래스는 인스턴스를 생성해서 사용하도록 설계한 클래스가 아닙니다. 
<br>


```java
UtilityClass.hello();				// 인스턴스를 생성하지 않고 호출

UtilityClass utilityClass = new UtilityClass();		
utilityClass.hello();				// 인스턴스를 생성하여 호출
```
인스턴스 메서드를 생성하여 메서드를 호출하는게 문법적으로 잘못된건 아니지만 바로 hello메서드를 호출할 수 있음에도 불필요하게 인스턴스를 생성하여 hello메서드가 정적 메서드인지 인스턴스 메서드인지 헷갈리게하는 코드입니다. 그래서 인스턴스생성을 방지하자라는게 이번 아이템의 주제 입니다.

클래스에 abstract를 추가하면 추상클래스로 생성되기 때문에 인스턴스 생성을 막을 수 있지 않냐고 생각할 수 있습니다.
#### 추상클래스로 선언
```java
public abstract class UtilityClass {

    public static String hello() {
        return "hello";
    }
}
```
하지만 추상클래스도 인스턴스로 생성될 수 있습니다. 


```java
public class DefaultUtilityClass extends UtilityClass {

    public static void main(String[] args) {
        DefaultUtilityClass utilityClass = new DefaultUtilityClass();
        utilityClass.hello();
    }
}
```
UtilityClass를 상속하는 DefaultUtilityClass의 인스턴스를 생성할때 부모인 UtilityClass의 생성자를 호출해 결국 인스턴스를 생성하게 됩니다. <br>
결론은 추상클래스로 인스턴스생성을 완전히 막을수 없고 abstract라는 키워드 때문에 이 클래스는 상속용도로 쓰는건가라고 착각할수 있기때문에 기존의 정적 유틸리티 메서드로 쓰려고하는 의도와는 맞지 않습니다.

#### 기본 생성자의 접근제한자를 private으로 변경하여 인스턴스 생성, 상속 방지

```java
public class UtilityClass {

    private UtilityClass() {
    }

    public static String hello() {
        return "hello";
    }
}
```

abstract키워드로 추상클래스를 만들지 않고 private 생성자로 해당 클래스 밖에서 인스턴스를 만들수 없게 할수 있습니다. 하지만 내부에서는 생성할 수 있습니다. 내부에서 까지 인스턴스 생성을 방지하려면 private 생성자를 호출할 때 AssertionError를 던지도록 합니다. 
```java
private UtilityClass() {
    throw new AssertionError();
}
```
AssertionError는 try-catch를 처리하도록 하는 예외는 아니고 발생되면 안되는 상황인데 혹시 발생되게 되면 무조건 예외가 아니라 에러를 던집니다. 결론적으로 private 생성자에 AssertionsError를 던지도록해서 내부에서도 인스턴스 생성을 방지할 수있습니다. 

그런데 다른사람이 코드를 읽다보면 굳이 생성자를 만들면서까지 못쓰게하는게 뭔가 이상하긴 합니다. 왜 못쓰는 코드를 굳이 구현하면서 만들어야하나 의아해 할 수 있기때문에 왜 private 생성자를 만들었는지 아래와 같이 주석으로 문서화하는 것을 추천하고 있습니다.
```java
public class UtilityClass {

    /**
     * 이 클래스는 인스턴스를 만들 수 없습니다.
     */
    private UtilityClass() {
    	throw new AssertionError();
    }

    public static String hello() {
        return "hello";
    }
}
```

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#
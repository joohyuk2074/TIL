자바는 JVM에서 가비지 컬렉터가 불필요한 메모리를 알아서 정리해주지만 스택, 캐시, 리스너 또는 콜백과 관련한 자기 메모리를 직접 관리하는 클래스라면 메모리 누수에 주의해야 합니다. 그래서 메모리누수가 발생할 수 있는 경우를 살펴보고 어떻게 메모리 누수를 막을 수 있는지 살펴보겠습니다.

### 스택

#### Java로 구현한 Stack
```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }

    /**
     * 원소를 위한 공간을 적어도 하나 이상 확보한다.
     * 배열 크기를 늘려야 할 때마다 대략 두 배씩 늘린다.
     */
    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```

pop메서드를 보면 size가 0일때는 EmptyStackException을 던지고 0이 아닐경우 계속 element의 참조값을 리턴하게 되는데 이런경우 elements배열에 계속 쌓이기만 합니다. 계속 push, pop을 반복하고 아무리 pop을 계속 한다고해도 힙이 꽉차서 메모리 누수가 생길수 있습니다.

#### 개선된 pop 메서드
```java
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }
```
메모리누수를 막기 위해 element를 pop할때 다쓴 객체의 참조를 해제하도록 합니다. 

Stack처럼 가비지컬렉션의 대상이 되지 않는 예외적인 경우는 뭔가를 담고있는 배열이나 List, Set처럼 직접 메모리를 관리해줘야 하는경우 메모리누수에 주의해야 합니다. 배열, List, Set에 쌓인 객체들이 언제 사라져야되는지 항상 염두해야 하고 개선된 pop 메서드에서 참조를 null로 해서 해제하는 방법이 메모리 누수를 방지하는 방법중에 하나고 굉장히 명시적입니다.

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#


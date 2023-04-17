equals를 재정의할때는 hashCode도 같이 재정의 해야합니다. IDE에서 equals를 재정의 할 때 hashCode를 같이 구현하도록 되어있습니다. lombok 또한 같은 어노테이션으로 equals와 hashCode가 묶여있습니다. 혹시나 equals가 재정의 되어있는데 hashCode가 재정이 되어 있지 않다면 잘못된 코드이므로 반드시 hashCode를 재정의 해야 합니다. 왜 equals를 재정의할 때 hasCode도 같이 재정의 해야하는지 살펴봅시다.
Object 명세에서 다음과 같은 규약이 있습니다
> - equals 비교에 사용하는 정보가 변경되지 않았다면 hashCode는 매번 같은 값을 리턴해야 한다. (변경되거나, 애플리케이션을 다시 실행했다면 달라질 수 있다.)
> - 두 객체에 대한 equals가 같다면, hashCode의 값도 같아야 한다.
> - 두 객체에 대한 equals가 다르더라도, hashCode의 값은 같을 수 있지만 해시 테이블 성능을 고려해 다른 값을 리턴하는 것이 좋다.

hashCode 재정의를 잘못했을 때 크게 문제가 되는 조항은 두 번째 입니다. 즉, 논리적으로 같은 객체는 같은 해시코드를 반환해야 합니다. HashMap을 통한 예제를 살펴보겠습니다. 

#### equals만 재정의 되어있고 hashCode는 재정의 하지 않은 PhoneNumber클래스

```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix   = rangeCheck(prefix,   999, "prefix");
        this.lineNum  = rangeCheck(lineNum, 9999, "line num");
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }
}
```
이 PhoneNumber 클래스의 인스턴스를 HashMap의 원소로 사용한다고 가정해봅시다.

```java
Map<PhoneNumber, String> m = new HashMap<>();
m.put(new PhoneNumber(707, 867, 5309), "제니");

System.out.println(m.get(new PhoneNumber(707, 867, 5309)));
```
이 코드의 m.get(new PhoneNumber(707, 867, 5309))를 실행하면 "제니"가 나와야 할 것 같지만, 실제로는 null값을 반환한다. 여기에는 2개의 인스턴스가 사용됐는데 하나는 HashMap에 "제니"를 넣을 때 사용됐고, 두 번째는 이를 꺼내려할 때 사용했다. HashMap은 put, get연산을 할 때 hashCode를 비교해서 실행하기 때문에 PhoneNumber클래스는 hashCode를 재정의 하지 않아서 논리적 동치인 두 객체가 서로 다른 해시코드를 반환하여 두번째 규약인 "두 객체에 대한 equals가 같다면, hashCode의 값도 같아야 한다."를 지키지 못합니다. 

그렇다면 hashCode를 재정의해서 같은값을 리턴하도록 재정의를 해봅시다.
```java
    @Override
    public int hashCode() {
        return 42;
    }
```
이렇게 hashCode값을 같은 값으로 반환하니 m.get(new PhoneNumber(707, 867, 5309))를 실행하면 null값이 아니라 "제니"를 출력할 수 있다. 하지만 모든 객체에 똑같은 hashCode값을 내려주기 때문에 모든 객체가 HashMap의 버킷 하나에 담겨 연결 리스트처럼 동작합니다. 그 결과 평균 수행 시간이 O(1)인 HashMap이 O(n)으로 느려져서, HashMap을 쓰는 이유가 무의미해 집니다.

### hashCode 구현 방법
좋은 해시 함수라면 서로 다른 인스턴스에 다른 해시코드를 반환해야합니다. 이것이 바로 hashCode를 세 번째 규약이 요구하는 속성인데 hashCode의 구현방법을 살펴보겠습니다.
#### 1. 첫번째 방법 - 전형적인 hashCode 메서드
```java
    @Override 
    public int hashCode() {
        int result = Short.hashCode(areaCode); // 1
        result = 31 * result + Short.hashCode(prefix); // 2
        result = 31 * result + Short.hashCode(lineNum); // 3
        return result;
    }

```
1: 가장 핵심필드중의 하나를 골라서 그 필드의 해시코드 값을 구합니다. 이 필드가 primitive타입이라면 해당하는 타입의 wrapper타입의 hashCode메서드를 사용해서 해쉬코드 값을 구하면 됩니다.
2: 남은 필드들을 순서대로 31 * result + (그 필드의 해시코드 값)을 더해서 result에 더해줍니다.
3: 2와 마찬가지로 더해서 리턴해주면 됩니다.

그렇다면 왜 하필 31을 곱해주는지 의문이 생길수도 있습니다. 두 가지 이유가 있습니다. 첫 번째는 홀수라는 점입니다. 짝수를 쓰게되면 뒤에 부분의 값이 0으로 채워져 오버플로가 발생해서 정보를 잃게될 수 있습니다. 두 번째는 홀수중에 31인 이유는 어떤 개발자분이 사전에 있는 모든 단어를 해싱 후 어떤 숫자를 썼을떄 가장 충돌이 적게나는지 테스트해서 나온 숫자입니다.

하지만 직접 저렇게 해시코드를 재정의 하지 않고 Objects클래스의 hash메서드를 사용하여 해시 값을 구할 수 있습니다
```java
    @Override public int hashCode() {
        return Objects.hash(lineNum, prefix, areaCode);
    }
```
hash메서드의 내부를 살펴보면 위에 직접 구현한 방법처럼 해시코드를 생성하는것을 확인할 수 있습니다. 

#### Objects.hash메서드의 내부

```java
    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
```
```java

    public static int hashCode(Object a[]) {
        if (a == null)
            return 0;

        int result = 1;

        for (Object element : a)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;

```
하지만 속도는 더 느린데 그 이유는 입력 인수를 담기 위한 배열이 만들어지고, 입력 중 기본 타입이 있다면 박싱과 언박싱도 거쳐야 하기 때문입니다. 그래서 hash를 사용하는 경우는 성능에 민감하지 않은 상황에서만 사용하길 추천합니다.

hashCode를 재정의하면서 주의해야할 점은 equals를 재정의하면서 사용된 필드를 hashCode에 누락시키면 2번째 규약을 어길 가능성이 있기때문에 빼지 않습니다.

#### 2. 두번째 방법 - 해시코드를 지연 초기화하는 hashCode
클래스가 불변이고 해시코드를 계산하는 비용이 크다면, 매번 새로 계산하기 보다는 캐싱하는 방식을 고려하는게 성능측면에서 좋습니다.

```java
    private volatile int hashCode; // 자동으로 0으로 초기화된다.

    @Override 
    public int hashCode() {
        if (this.hashCode != 0) {
            return hashCode;
        }

        synchronized (this) {		// 스레드 안전성을 고려한다
            int result = hashCode;
            if (result == 0) {
                result = Short.hashCode(areaCode);
                result = 31 * result + Short.hashCode(prefix);
                result = 31 * result + Short.hashCode(lineNum);
                this.hashCode = result;
            }
            return result;
        }
    }
```
여러 스레드가 동시에 hashCode를 호출하게되면 result값이 변경되어 잘못된 해시코드를 생성할 수 있기때문에 synchronized를 선언하여 어떤 스레드가 hashCode를 실행할때 다른스레드가 실행할수 없게 lock을 걸어 줍니다.

### 심화학습
- https://dzone.com/articles/hashmap-performance

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#


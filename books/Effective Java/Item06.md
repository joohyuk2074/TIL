객체들을 사용하다보면 불필요하게 여러번 생성하는 경우가 있습니다. 세가지 예를 살펴보겠습니다.
### 1. 문자열 생성
```java
String hello1 = "hello";
String hello2 = new String("hello");
```
보통 문자열을 생성할때 hello1처럼 사용하지만 hello2처럼 new 연산자를 사용하게되면 jvm은 문자열을 내부적으로 Pool에 캐싱을 하고 있다고 생각하시면 됩니다. 일종의 해시맵에 새로 생성된 문자열을 저장하고 어디선가 동일한 문자열을 생성하려고하면 새로생성하는 것이 아니라 이미 생성된 상수풀에서 동일한 문자열을 참조하도록 하는 방법으로 문자열을 재사용하기 때문에 new연산자를 통해서 문자열을 생성한다면 상수풀에 동일한문자가 있든 없든 강제로 문자열을 새로 생성하게 됩니다.

#### 문자열 비교
```java
public class Strings {

    public static void main(String[] args) {
        String hello = "hello";
        //TODO 이 방법은 권장하지 않습니다.
        String hello2 = new String("hello");


        System.out.println(hello == hello2);			// false
        System.out.println(hello.equals(hello2));		// true
    }
}
```
같은 hello문자열이지만 hello와 hello2는 인스턴스가 다르기때문에 false가 나오고 equals를 통해서 실질적인 문자열 내용자체를 비교하면 true가 나옵니다.

### 2. 정규식 생성
정규표현식은 한번 만들때 CPU리소스를 많이 사용하기 때문에 매번 인스턴스를 생성하기는 부답스럽습니다. 

#### 2.1 비효율적인 정규식 활용
```java
public class RomanNumerals {
    // 코드 6-1 성능을 훨씬 더 끌어올릴 수 있다!
    static boolean isRomanNumeralSlow(String s) {
        return s.matches("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    }
}
```

##### 실행시간 측정
```java
    public static void main(String[] args) {
        boolean result = false;
        long start = System.nanoTime();
        for (int j = 0; j < 100; j++) {
            result = isRomanNumeralSlow("MCMLXXVI");
        }
        long end = System.nanoTime();
        System.out.println(end - start);
        System.out.println(result);
    }
```
##### 결과
```
17322178
true
```
문자열을 matches메서드를 사용해서 정규식에 포함되는지 실행할때 matches메서드 인자로 정규식을 받는데 내부적으로 컴파일을 하게 됩니다. 하지만 이 패턴을 만들어서 컴파일하는 과정이 오래걸리는데 isRomanNumeralSlow메서드를 반복적으로 호출을 하는것은 굉장히 비효율적입니다.

#### 2.2 효율적인 정규식사용
```java
public class RomanNumerals {
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeralFast(String s) {
        return ROMAN.matcher(s).matches();
    }

}
```
동일한패턴이 여러번 사용이 된다면 필드를 선언해서 사용하는것을 책에서 권장하고 있습니다.
##### 실행시간 측정
```java
    public static void main(String[] args) {
        boolean result = false;
        long start = System.nanoTime();
        for (int j = 0; j < 100; j++) {
            result = isRomanNumeralFast("MCMLXXVI");
        }
        long end = System.nanoTime();
        System.out.println(end - start);
        System.out.println(result);
    }
```
##### 실행 결과
```
2249606
true
```

실행시간이 17322178 -> 2249606 6배정도로 단축된것을 알 수 있다.

### 3. 오토박싱과 언박싱

#### 3.1 불필요한 오토박싱이 일어나는 경우
```java
public class Sum {
    private static long sum() {
        Long sum = 0L;
        for (long i = 0; i <= Integer.MAX_VALUE; i++)
            sum += i;	// 불필요한 오토박싱 발생 long -> Long
        return sum;
    }
}
```
jvm에서 자동으로 long인 primitive타입을 Long인 sum에 더하기위해 자동으로 오토박싱을 해서 Wrapper타입인 Long으로 바꿔서 더해주게 됩니다.

##### 실행시간 측정
```java
    public static void main(String[] args) {
        long start = System.nanoTime();
        long x = sum();
        long end = System.nanoTime();
        System.out.println((end - start) / 1_000_000. + " ms.");
        System.out.println(x);
    }
```

##### 실행 결과
```
7849.698364 ms.
2305843008139952128
```

#### 3.2 오토박싱이 일어나지 않는경우
```java
public class Sum {
    private static long sum() {
        long sum = 0L;
        for (long i = 0; i <= Integer.MAX_VALUE; i++)
            sum += i;	// 오토박싱이 일어나지 않음
        return sum;
    }
}
```

##### 실행시간 측정
```java
    public static void main(String[] args) {
        long start = System.nanoTime();
        long x = sum();
        long end = System.nanoTime();
        System.out.println((end - start) / 1_000_000. + " ms.");
        System.out.println(x);
    }
```

##### 실행결과
```
1555.739637 ms.
2305843008139952128
```

실행시간이 7849.698364 ms. -> 1555.739637 ms. 5배정도 줄어든것을 확인할 수 있습니다.

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#
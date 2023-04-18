#### toString을 재정의 하지 않은 PhoneNumber클래스
```java
package me.whiteship.chapter02.item12;

import lombok.ToString;

// PhoneNumber에 toString 메서드 추가 (75쪽)
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "지역코드");
        this.prefix   = rangeCheck(prefix,   999, "프리픽스");
        this.lineNum  = rangeCheck(lineNum, 9999, "가입자 번호");
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override 
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }

    @Override 
    public int hashCode() {
        int result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        return result;
    }

	// getter, setter
}
```

Object에서 기본적으로 제공하는 toString같은 경우에는 클래스이름뒤에 16진수로 표현한 해쉬코드를 반환합니다.
```java
PhoneNumber jenny = new PhoneNumber(707, 867, 5309);
System.out.println("제니의 번호: " + jenny);
```
실행결과
```
me.whiteship.chapter02.item12.PhoneNumber@adbbd
```
위와 같은 실행결과는 딱히 유용하지가 않습니다. 따라서 간결하면서 사람이 일기 쉬운 형태의 유익한 정보를 반환해야합니다. toString을 잘 구현한 클래스는 사용하기에 훨씬 즐겁고, 그 클래스를 사용한 시스템은 디버깅하기 쉽습니다.

#### 어떤 문자열로 변환해주는지 문서화 추가

```java

    /**
     * 이 전화번호의 문자열 표현을 반환한다.
     * 이 문자열은 "XXX-YYY-ZZZZ" 형태의 12글자로 구성된다.
     * XXX는 지역 코드, YYY는 프리픽스, ZZZZ는 가입자 번호다.
     * 각각의 대문자는 10진수 숫자 하나를 나타낸다.
     *
     * 전화번호의 각 부분의 값이 너무 작아서 자릿수를 채울 수 없다면,
     * 앞에서부터 0으로 채워나간다. 예컨대 가입자 번호가 123이라면
     * 전화번호의 마지막 네 문자는 "0123"이 된다.
     */
    @Override 
    public String toString() {
        return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }
```
toString을 재정의할 때 특히 값클래스에서 구현할 때는 javadoc으로 어떤 문자열로 변환 해주는지 문서화를 하는것이 좋습니다.


Effective Java의 저자 조슈아 블로크는 toString재정의 할 때 객체가 가진 주요 정보 모두를 반환하는 게 좋다고 했는데 강의를 하시는 백기선님의 입장은 모두 공개하는 것을 위험할 수 있고 실제로 아마존에서는 민감한 정보들이 많아서 public한 정보만 공개하는게 맞는것 같다는 입장입니다. 제 개인적인 생각도 예를들어 고객이 어떤 상품을 주문했는지, 어떤 주소로 배송했는지와 같은 개인정보들은 노출하지 않는게 좋다고 생각합니다.

```java
    @Override 
    public String toString() {
        return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }
```
하지만 toString에 재정의 되어 있는 것처럼 areaCode, prefix, lineNum을 전달받는 방법이 따로 있어야합니다. 따라서 위의정보를 받을 수 있도록 정적팩토리 메서드를 추가할 수 있습니다.

#### 정적 팩토리 메서드 추가
```java
    public static PhoneNumber of(String phoneNumberString) {
        String[] split = phoneNumberString.split("-");
        PhoneNumber phoneNumber = new PhoneNumber(
                Short.parseShort(split[0]),
                Short.parseShort(split[1]),
                Short.parseShort(split[2]));
        return phoneNumber;
    }
```
정적팩토리 메서드를 생성해서 나중에 사용자가 받았던 문자열을 기반으로 PhoneNumber라는 인스턴스를 만들 수 있도록 구현합니다.

#### 실행
```java
    public static void main(String[] args) {
        PhoneNumber jenny = new PhoneNumber(707, 867, 5309);
        System.out.println("제니의 번호: " + jenny);

        PhoneNumber phoneNumber = PhoneNumber.of("707-867-5309");
        System.out.println(phoneNumber);
        System.out.println(jenny.equals(phoneNumber));
        System.out.println(jenny.hashCode() == phoneNumber.hashCode());
    }
```
#### 실행결과
```java
제니의 번호: 707-867-5309
707-867-5309
true
true
```

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#


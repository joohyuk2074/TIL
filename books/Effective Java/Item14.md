# 아이템 14. Comparable을 구현할지 고민하라
Comparable은 자연적인 순서를 정해줄때 필요한 인터페이스입니다. Comparable을 사용하면 비교를 통해 순서를 정하고 싶을때 순서를 정하는 방법을 구현할 수 있습니다. 또한 Comparable은 제네릭타입을 가지고있기 때문에 컴파일시점에 타입체킹이 가능하다는 장점이 있습니다. Object에서 제공하는 equals와 굉장히 비슷한데 다른점은 순서를 비교할 수 있고 제네릭타입을 지원한다는 차이점이 있습니다. Comparable인터페이스의 compareTo메서드를 재정의할 때 규약이 있는데 어떻게 구현해야할지 살펴보겠습니다.

#### Comparable인터페이스를 상속받아 compareTo를 재정의하는 BigDecimal클래스
```java
public class BigDecimal extends Number implements Comparable<BigDecimal> {
  
  	// ....             
                              
    @Override
    public int compareTo(BigDecimal val) {
        // Quick path for equal scale and non-inflated case.
        if (scale == val.scale) {
            long xs = intCompact;
            long ys = val.intCompact;
            if (xs != INFLATED && ys != INFLATED)
                return xs != ys ? ((xs > ys) ? 1 : -1) : 0;
        }
        int xsign = this.signum();
        int ysign = val.signum();
        if (xsign != ysign)
            return (xsign > ysign) ? 1 : -1;
        if (xsign == 0)
            return 0;
        int cmp = compareMagnitude(val);
        return (xsign > 0) ? cmp : -cmp;
    }
    
    // ...

}
```
BigDecimal클래스가 대표적으로 Comparable인터페이스를 구현하고 있습니다. BigDecimal의 compareTo를 통해서 어떤 규약을 지켜야하는지 살펴봅시다.

#### compareTo재정의시 규약 조건

```java
import java.math.BigDecimal;

public class CompareToConvention {

    public static void main(String[] args) {
        BigDecimal n1 = BigDecimal.valueOf(23134134);
        BigDecimal n2 = BigDecimal.valueOf(11231230);
        BigDecimal n3 = BigDecimal.valueOf(53534552);
        BigDecimal n4 = BigDecimal.valueOf(11231230);

        // p89, 일관성
        System.out.println(n4.compareTo(n2));
        System.out.println(n2.compareTo(n1));
        System.out.println(n4.compareTo(n1));

        // p89, compareTo가 0이라면 equals는 true여야 한다. (아닐 수도 있고..)
        BigDecimal oneZero = new BigDecimal("1.0");
        BigDecimal oneZeroZero = new BigDecimal("1.00");
        System.out.println(oneZero.compareTo(oneZeroZero)); // Tree, TreeMap
        System.out.println(oneZero.equals(oneZeroZero)); // 순서가 없는 콜렉션
    }
}
```
- 반사성
```java
BigDecimal n1 = BigDecimal.valueOf(23134134);
System.out.println(n1.compareTo(n1));	// 0
```
당연히 자기자신과 비교를 했을때 같다고하는 반사성을 지켜야합니다.

- 대칭성
```java
BigDecimal n1 = BigDecimal.valueOf(23134134);
BigDecimal n2 = BigDecimal.valueOf(11231230);
       
System.out.println(n1.compareTo(n2));	// 1
System.out.println(n2.compareTo(n1));	// -1
```
n1이 n2보다 크기때문에 n1기준으로 n2를 비교하면 1을 리턴하고 n2기준으로 n1을 비교하면 -1이 리턴되야 합니다. 한쪽이 1이나오면 반대쪽에서 -1이나오는 대칭성을 만족해야 합니다.

- 추이성
```java
BigDecimal n1 = BigDecimal.valueOf(23134134);
BigDecimal n2 = BigDecimal.valueOf(11231230);
BigDecimal n3 = BigDecimal.valueOf(53534552);

System.out.println(n3.compareTo(n1) > 0);	// true
System.out.println(n1.compareTo(n2) > 0);	// true
System.out.println(n3.compareTo(n2) > 0);	// true
```
n3 > n1이고 n1 > n2면 n3 > n2를 만족해야합니다.

- 일관성
```java
BigDecimal n1 = BigDecimal.valueOf(23134134);
BigDecimal n2 = BigDecimal.valueOf(11231230);
BigDecimal n4 = BigDecimal.valueOf(11231230);

System.out.println(n4.compareTo(n2));	// 0
System.out.println(n2.compareTo(n1));	// -1
System.out.println(n4.compareTo(n1));	// -1
```
만약 n2 == n4라면 n2를 n1과 비교한 결과와 n4를 n1과 비교한 결과가 같아야 합니다.

### Comparable 구현 방법
우리가 직접 생성한 클래스에 비교를 통해 순서를 정하고싶다면 해당 클래스에 Comparable인터페이스를 상속받아서 compareTo를 구현하면 됩니다.
```java
public final class PhoneNumber implements Comparable<PhoneNumber> {
	// ...
    
    @Override
    public int compareTo(PhoneNumber pn) {
        int result = Short.compare(areaCode, pn.areaCode);
        if (result == 0)  {
            result = Short.compare(prefix, pn.prefix);
            if (result == 0)
                result = Short.compare(lineNum, pn.lineNum);
        }
        return result;
    }
}
```
여기서는 this의 areaCode와 인자로 넘어온 PhoneNumber객체의 areaCode를 제일 우선적으로 비교하고 같다면 prefix를 비교, prefix도 같다면 lineNum을 비교해서 areaCode, prefix, lineNum순서로 비교하여 순서를 정하도록 구현했습니다. 

#### 주의할 점
```java
public class Point implements Comparable<Point>{

    final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int compareTo(Point point) {
        int result = Integer.compare(this.x, point.x);
        if (result == 0) {
            result = Integer.compare(this.y, point.y);
        }
        return result;
    }
}
```

```java
public class NamedPoint extends Point {

    final private String name;

    public NamedPoint(int x, int y, String name) {
        super(x, y);
        this.name = name;
    }
}
```
하지만 한가지 주의해야할 점은 x,y좌표를 비교해서 순서를 정하는 Point클래스를 상속하는 NamedPoint가 있는데 name으로도 비교를 하고싶을때 Comparable을 상속받아 재정의할 수 없습니다. 이미 Point클래스에서 Comparable을 상속하고 제네릭으로 Point타입을 설정했기때문에 NamedPoint를 제네릭으로 설정할 수 없습니다.

#### TreeSet에 Comparator를 정의해서 비교(추천하지 않는방법)
```java
NamedPoint p1 = new NamedPoint(1, 0, "keesun");
NamedPoint p2 = new NamedPoint(1, 0, "whiteship");

Set<NamedPoint> points = new TreeSet<>(new Comparator<NamedPoint>() {
    @Override
    public int compare(NamedPoint p1, NamedPoint p2) {
        int result = Integer.compare(p1.getX(), p2.getX());
        if (result == 0) {
            result = Integer.compare(p1.getY(), p2.getY());
        }
        if (result == 0) {
            result = p1.name.compareTo(p2.name);
        }
        return result;
    }
});

points.add(p1);
points.add(p2);

System.out.println(points);
```
NamedPoint에서 Comparable을 설정하지 않고 TreeSet에 정렬기준으로 Comparator를 구현해서 인자로 넘기면 name을 포함해서 비교하여 순서를 정렬할 수 있습니다.

실행 결과
```
[NamedPoint{name='keesun', x=1, y=0}, NamedPoint{name='whiteship', x=1, y=0}]
```
하지만 Point를 상속받아서 name필드를 추가하면 equals규약이 깨지게 됩니다. 그래서 이런방법보다 책에서는 Composition을 사용하는방법을 추천하고있습니다.

#### Composition을 사용하는 방법
```java
public class Point implements Comparable<Point>{

    final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Point point) {
        int result = Integer.compare(this.x, point.x);
        if (result == 0) {
            result = Integer.compare(this.y, point.y);
        }
        return result;
    }
}
```
위의 Point클래스를 확장에서 필드를 추가하는 방법보다 컴포지션을 사용하여 Point를 참조하도록 해서 생성한 클래스에서 따로 compareTo를 재정의하면 됩니다.

```java
public class NamedPoint implements Comparable<NamedPoint> {

    private final Point point;
    private final String name;

    public NamedPoint(Point point, String name) {
        this.point = point;
        this.name = name;
    }

    public Point getPoint() {
        return this.point;
    }

    @Override
    public int compareTo(NamedPoint namedPoint) {
        int result = this.point.compareTo(namedPoint.point);
        if (result == 0) {
            result = this.name.compareTo(namedPoint.name);
        }
        return result;
    }
}
```
컴포지션으로 Point를 참조하고 name필드를 추가하면 equals규약도 깨지지 않고 NamedPoint타입으로 비교를 할 수 있도록 정의할 수 있습니다.

#### Java8 이후 compareTo 생성방법
```java
// 코드 14-3 비교자 생성 메서드를 활용한 비교자 (92쪽)
    private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.getPrefix())
                    .thenComparingInt(pn -> pn.lineNum);

    @Override
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
```
Comparator의 static메서드를 통해서 Comparable의 compareTo메서드를 재정의 할 수 있습니다.


### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#
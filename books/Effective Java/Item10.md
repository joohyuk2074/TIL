자바의 모든 클래스는 Object클래스를 암묵적으로 상속받고 있습니다. 따라서 어떤 클래스를 생성할 때 Object의 메서드를 오버라이드할 수 있습니다. 그 중 대표적으로 오버라이드 할 수 있는게 equals, toString, clone, hashCode, finalize등이 있습니다. equals의 경우는 직접 구현하는 경우와 구현하지 않고 Object의 equals를 그대로 사용하는 경우가 있습니다. 보통 직접 구현을 하더라도 툴의 기능을 사용해서 구현하게 됩니다. 예를 들어 롬복같은 라이브러리를 사용해서 어노테이션을 붙여서 사용하기도 합니다.

우선 언제 equals를 구현해야하는지 구현할 필요가 없는지를 먼저 구분해야 합니다. 책에서는 일단 만들지 않는게 최선이라고 말합니다. 따라서 언제 오버라이드 할 필요가 없는지 살펴봅시다.

### equals를 재정의 할 필요가 없는 경우
#### 1. 싱글턴을 보장하는 객체의 경우
싱글턴 같은경우 객체가 한개만 유지하기때문에 고유합니다. 따라서 비교를 하기위한 equals를 굳이 오버라이드 할 필요가 없습니다. Enum도 마찬가지 입니다.
#### 2. 논리적으로 동치성을 검사할 필요가 없는경우
```java
public class Pizza {
	private String name;
    
    public Pizza(String name) {
    	this.name = name;
    }
}
```

```java
Pizza pizza1 = new Pizza("페퍼로니");
Pizza pizza2 = new Pizza("페퍼로니");
```
```java
pizza1.equals(pizza2) // false
```
Pizza라는 클래스가 있다고 합시다. 이 Pizza name이 같은 두개의 객체를 생성하면 페퍼로니 피자라고 개념적으로는 같은 Pizza가 생성이 됩니다. 하지만 실제로 new 연산자로 각각 다른 인스턴스가 생성되었기 때문에 논리적으로는 같은 페퍼로니피자지만 주소값이 다른 별개의 객체입니다. 이 피자가 논리적으로 name이 같은 피자인지를 판단할 게 아니라면 굳이 equals를 재정의 할 필요가 없습니다.
#### 3. 상위 클래스에 이미 재정의가 되어있는 경우
상위클래스에 재정이 된 equals메서드가 있다면 굳이 새로 재정의할 필요가 없습니다.
#### 4. 클래스가 private이나 package-private인 경우

### equals를 재정의 해야할 필요가 있는 경우
위의 경우가 아니라면 equals를 재정의 해야하는데 재정의할 때 Object클래스의 equals메서드의 문서를 살펴보면 규약이 존재합니다. 그 규약은 반사성, 대칭성, 추이성, 일관성을 따라야 합니다.
#### 1. 반사성: A.equals(A) == true
#### 2. 대칭성: A.eqauls(B) == B.equals(A)
##### 코드 10-1 잘못된 코드 - 대칭성 위배! (54-55쪽)
```java
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

	// 대칭성 위배!
    @Override 
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        if (o instanceof String)  // 한 방향으로만 작동한다!
            return s.equalsIgnoreCase((String) o);
        return false;
    }
}
```

```java
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String polish = "polish";
        System.out.println(cis.equals(polish));
        System.out.println(cis2.equals(cis));

```
```
true
false
```
CaseInsensitiveString에서 equals메서드로 polish를 비교하면 재정의한 equals메서드에서 CaseInsensitiveString타입인 경우 String 타입으로 강제 형변환 후 비교하도록 재정의가 되어있지만 반대로 String타입인 polish에서 CaseInsensitiveString을 비교하면 String에 equals메서드에는 CaseInsensitiveString이라는 타입을 모르기때문에 결과가 false가 나와 대칭성을 위배합니다. 따라서 저런 equals재정의는 피해야 합니다.

##### 수정한 equals 메서드 (56쪽)
```java
    @Override public boolean equals(Object o) {
        return o instanceof CaseInsensitiveString &&
                ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
    }
```
#### 3. 추이성: A.equals(B) && B.equals(C), A.equals(C)
```java
package me.whiteship.chapter02.item10.inheritance;

import me.whiteship.chapter02.item10.Color;
import me.whiteship.chapter02.item10.Point;

// Point에 값 컴포넌트(color)를 추가 (56쪽)
public class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    // 코드 10-2 잘못된 코드 - 대칭성 위배! (57쪽)
//    @Override public boolean equals(Object o) {
//        if (!(o instanceof ColorPoint))
//            return false;
//        return super.equals(o) && ((ColorPoint) o).color == color;
//    }

//    // 코드 10-3 잘못된 코드 - 추이성 위배! (57쪽)
    @Override public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;

        // o가 일반 Point면 색상을 무시하고 비교한다.
        if (!(o instanceof ColorPoint))
            return o.equals(this);

        // o가 ColorPoint면 색상까지 비교한다.
        return super.equals(o) && ((ColorPoint) o).color == color;
    }

    public static void main(String[] args) {
        // 첫 번째 equals 메서드(코드 10-2)는 대칭성을 위배한다. (57쪽)
//        Point p = new Point(1, 2);
//        ColorPoint cp = new ColorPoint(1, 2, Color.RED);
//        System.out.println(p.equals(cp) + " " + cp.equals(p));

        // 두 번째 equals 메서드(코드 10-3)는 추이성을 위배한다. (57쪽)
        ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
        Point p2 = new Point(1, 2);
        ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);
        System.out.printf("%s %s %s%n",
                          p1.equals(p2), p2.equals(p3), p1.equals(p3));
    }
}
```

```java
package me.whiteship.chapter02.item10.inheritance;


import me.whiteship.chapter02.item10.Color;
import me.whiteship.chapter02.item10.Point;

import java.util.Set;

// CounterPoint를 Point로 사용하는 테스트 프로그램
public class CounterPointTest {
    // 단위 원 안의 모든 점을 포함하도록 unitCircle을 초기화한다. (58쪽)
    private static final Set<Point> unitCircle = Set.of(
            new Point( 1,  0), new Point( 0,  1),
            new Point(-1,  0), new Point( 0, -1));

    public static boolean onUnitCircle(Point p) {
        return unitCircle.contains(p);
    }

    public static void main(String[] args) {
        Point p1 = new Point(1,  0);
        Point p2 = new CounterPoint(1, 0);

        // true를 출력한다.
        System.out.println(onUnitCircle(p1));

        // true를 출력해야 하지만, Point의 equals가 getClass를 사용해 작성되었다면 그렇지 않다.
        System.out.println(onUnitCircle(p2));
    }
}

```
##### 컴포지션으로 개선하는 경우
```java
package me.whiteship.chapter02.item10.composition;


import me.whiteship.chapter02.item10.Color;
import me.whiteship.chapter02.item10.Point;

import java.util.Objects;

// 코드 10-5 equals 규약을 지키면서 값 추가하기 (60쪽)
public class ColorPoint {
    private final Point point;
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    /**
     * 이 ColorPoint의 Point 뷰를 반환한다.
     */
    public Point asPoint() {
        return point;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
            return false;
        ColorPoint cp = (ColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }

    @Override public int hashCode() {
        return 31 * point.hashCode() + color.hashCode();
    }
}
```
#### 4. 일관성: A.equals(B) == A.equals(B)
#### 5. null-아님: A.equals(null) == false


### equals 구현 방법과 주의사항
equals를 재정의할 때 위의 5가지 사항을 고려하면서 재정의하면 됩니다.

```java
    @Override public boolean equals(Object o) {
        if (this == o) {	// 객체의 동일성을 판단
            return true;
        }

        if (!(o instanceof Point)) {	// 타입을 비교
            return false;
        }

        Point p = (Point) o;		   // 타입캐스팅
        return p.x == x && p.y == y;  
    }

```

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#

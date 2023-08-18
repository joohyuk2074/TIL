## 아이템 15. 클래스와 멤버의 접근 권한을 최소화하라.
### 1. 구현과 API를 분리하는 "정보 은닉"의 장점
- 시스템 개발 속도를 높인다. (여러 컴포넌트를 병렬로 개발할 수 있기 떄문에)
- 시스템 관리 비용을 낮춘다. (컴포넌트를 더 빨리 파악할 수 있기 때문에)
- 성능 최적화에 도움을 준다. (프로파일링을 통해 최적화할 컴포넌트를 찾고 다른 컴포넌트에 영향을 주지 않고 해당 컴포넌트만 개선할 수 있기 때문에)
- 소프트웨어 재사용성을 높인다. (독자적인 컴포넌트라면)
- 시스템 개발 난이도를 낮춘다. (전체를 만들기 전에 개별 컴포넌트를 검증할 수 있기 때문에)

### 2. 클래스와 인터페이스의 접근 제한자 사용 원칙
- 모든 클래스와 멤버의 접근성을 가능한 한 좁혀야한다.
- 톱레벨 클래스와 인터페이스에 package-private 또는 public을 쓸 수 있다
	- public으로 선언하면 API가 되므로 하위 호환성을 유지하려면 영원히 관리해야 한다.
    - 패키지 외부에서 쓰지 않을 클래스나 인터페이스라면 package-private으로 선언한다.
- 한 클래스에서만 사용하는 package-private 클래스나 인터페이스는 해당 클래스에 private static으로 중첩 시키자.

#### 한 클래스에서만 사용하는 package-private 클래스나 인터페이스는 해당 클래스에 private static으로 중첩 시키자.
왜 저자는 하필 private static으로 중첩시키자고 하는지 궁금합니다.

```java
class DefaultMemberService implements MemberService {

    private String name;

    private static class PrivateStaticClass {		// private static inner class

    }

    private class PrivateClass {					// inner class

    }
}
```
차이를 살펴보면 inner PrivateClass는 PrivateClass를 감싸고있는 DefaultMemberService의 인스턴스를 참조합니다. 하지만 inner PrivateStaticClass는 inner 클래스이지만 독립적인거나 마찬가지 입니다. 따라서 PrivateStaticClass는 DefaultMemberService인스턴스의 참조가 없습니다. 

#### inner클래스인 PrivateClass의 필드들을 호출
```java
    public static void main(String[] args) {
        Arrays.stream(PrivateClass.class.getDeclaredFields()).forEach(System.out::println);
    }
```
실행결과
```
final me.whiteship.chapter04.item15.class_and_interfaces.member.DefaultMemberService me.whiteship.chapter04.item15.class_and_interfaces.member.DefaultMemberService$PrivateClass.this$0
```
Privateclass의 필드를 출력해보면 DefaultMemberService가 출력되는것을 확인할 수 있습니다.


```java
class DefaultMemberService implements MemberService {

    private String name;

    private static class PrivateStaticClass {		// private static inner class
		void doPrint() {
        	System.out.println(name);	// 불가능
        }
    }

    private class PrivateClass {					// inner class
		void doPrint() {
        	System.out.println(name);
        }
    }
}
```
PrivateClass는 DefaultMemberService의 인스턴스를 참조하기때문에 name을 PrivateClass내에 doPrint메서드에서 자연스럽게 쓸 수 있습니다. PrivateStaticClass는 불가능 합니다. DefaultMemberService에서만 사용되는 inner클래스이지만 본질은 독립적인 클래스입니다. 따라서 참조를 갖는 PrivateClass보다는 독립적인 PrivateStaticClass가 DefaultMemberService와 관계가 단순해지고 더 적합해 보입니다.

### 3. 멤버(필드, 메서드, 중첩 클래스/인터페이스)의 접근 제한자 원칙
- private과 package-private은 내부 구현
- public 클래스의 protected와 public은 공개 API
- 코드를 테스트 하는 목적으로 private을 package-private으로 풀어주는 것은 허용할 수 있다. 하지만 테스트만을 위해서 멤버를 공개 API로 만들어서는 안된다. (테스트를 같은 패키지에 만든다면 그럴 필요도 없다)
- public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.
- 클래스에서 public static final 배열 필드를 두거나 이 필드를 반환하는 접근자 메서드를 제공해서는 안된다.

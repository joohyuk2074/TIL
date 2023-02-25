## 1. 생성자 체이닝과 자바빈즈

```java

public class NutritionFacts {
    private final int servingSize;  // (mL, 1회 제공량)     필수
    private final int servings;     // (회, 총 n회 제공량)  필수
    private final int calories;     // (1회 제공량당)       선택
    private final int fat;          // (g/1회 제공량)       선택
    private final int sodium;       // (mg/1회 제공량)      선택
    private final int carbohydrate; // (g/1회 제공량)       선택

    public NutritionFacts(int servingSize, int servings,
                          int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize  = servingSize;
        this.servings     = servings;
        this.calories     = calories;
        this.fat          = fat;
        this.sodium       = sodium;
        this.carbohydrate = carbohydrate;
    }
}

```
위의 코드를 살펴보면 생성자에 선택적 매개변수가 많은 경우가 있습니다. 

NutritionFacts의 인스턴스를 생성할 때 필수가 아닌 선택적인 값들까지 억지로 생성자의 인자로 0으로 설정해야 하는 불편함이 있습니다.
```java
NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 0, 0);
```
<br>

그래서 이러한 불편함을 극복하기 위해 점층적 생성자 패턴을 이용해 아래와 같이 클래스를 작성할 수 있습니다.

```java
public class NutritionFacts {
    private final int servingSize;  // (mL, 1회 제공량)     필수
    private final int servings;     // (회, 총 n회 제공량)  필수
    private final int calories;     // (1회 제공량당)       선택
    private final int fat;          // (g/1회 제공량)       선택
    private final int sodium;       // (mg/1회 제공량)      선택
    private final int carbohydrate; // (g/1회 제공량)       선택
    
    public NutritionFacts(int servingSize, int servings) {		
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings,
                          int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings,
                          int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize  = servingSize;
        this.servings     = servings;
        this.calories     = calories;
        this.fat          = fat;
        this.sodium       = sodium;
        this.carbohydrate = carbohydrate;
    }
}
````
점층적 생성자 패턴을 이용하면 필수 값인 servingSize와 servings만 설정이 필요한 경우 깔끔하게 인스턴스를 생성할 수 있습니다.
```java
NutritionFacts cocaCola = new NutritionFacts(240, 8);
```
<br>
하지만 선택적인 값들을 전달할때 필요한 점층적 생성자를 생성하다보니 매개변수가 많아지면 클라이너트 코드를 작성하거나 읽기가 어려워 집니다. 코드를 읽을 때 각 값의 의미가 무엇인지 헷갈리고 매개변수가 몇 개인지도 주의해서 세어 보아야 합니다. 결국 클라이언트가 실수로 매개변수의 순서를 바꿔 건네줘도 컴파일러는 알아채지 못하고, 런타임에 엉뚱한 동작을 하게되어 오류를 찾는데 상당히 어려워 집니다. 
<br>
물론 요즘 intellij가 cmd + p 를 통해서 각각의 생성자가 받는 파라미터의 타입과 이름을 알려주기는 하지만 직관적으로 파악하기는 힘들고 어디까지나 저 기능이 있는 IDE를 쓸때만 입니다.

그래서 다른 대안으로 자바 빈즈패턴을 쓰는 방법이 있는데 자바빈즈패턴은 자바 표준 스펙중의 하나입니다. 매개변수가 없는 생성자로 객체를 만든 후, setter메서드를 호출해서 원하는 매개변수의 값을 설정하는 방식입니다.
```java
NutritionFacts cocaCola = new NutritionFacts();
cocaCola.setServingSize(240);
cocaCola.setServings(8);
cocaCola.setCalories(100);
cocaCola.setSodium(35);
cocaCola.setCarbohydrate(27);
```
이 방식의 장점은 객체를 간단히 생성할 수 있습니다. 하지만 setter메서드를 호출하지 않고 필수값들이 세팅이 안된 상태로 객체가 사용이 될 수 있고 어디까지 setter로 세팅을 해줘야하는지 문서화를 하지 않고는 알수가 없습니다.

결국 2가지를 혼용해서 필드의 값이 필수로 설정이 되어야 한다면 생성자를 통해서 강제로 인자를 전달하고 선택적인 필드는 setter메서드로 받는방법이 있습니다. 
```java
NutritionFacts cocaCola = new NutritionFacts(240, 8);
cocaCola.setCalories(100);
cocaCola.setSodium(35);
cocaCola.setCarbohydrate(27);
```
이 방법도 역시 setter로 객체를 설정하다보니 객체를 immutable하게 만들수가 없습니다. 그래서 이 단점을 해결하기위해 EffectiveJava에서는 생성이 끝난 객체를 수동으로 freezing이라는 방법이 있는데 다루기도 어렵고 실전에서 거의 쓰이지 않기때문에 다른 대안이 필요합니다.

## 2. 빌더
이럴때 점층적 생성자 패턴의 안전성과 자바 빈즈 패턴의 가독성을 겸비한 빌더 패턴을 고려할 수 있습니다.
```java
// 코드 2-3 빌더 패턴 - 점층적 생성자 패턴과 자바빈즈 패턴의 장점만 취했다. (17~18쪽)
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화한다.
        private int calories      = 0;
        private int fat           = 0;
        private int sodium        = 0;
        private int carbohydrate  = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings    = servings;
        }

        public Builder calories(int val)
        { calories = val;      return this; }
        public Builder fat(int val)
        { fat = val;           return this; }
        public Builder sodium(int val)
        { sodium = val;        return this; }
        public Builder carbohydrate(int val)
        { carbohydrate = val;  return this; }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize  = builder.servingSize;
        servings     = builder.servings;
        calories     = builder.calories;
        fat          = builder.fat;
        sodium       = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
```
NutritionFacts클래스 내부에 Builder인 정적 멤버 클래스로 생성하고 필드값은 NutritionFacts와 똑같이 설정합니다. 필드값이 필수인 부분은 생성자를 통하여 받고 선택적으로 값을 넣어야하는 필드들은 각각 메서드를 통해서 받고 반환타입을 Builder를 하여 그 객체 자체를 리턴합니다. 


최종적으로 build메서드를 통해서 NutritionFacts의 인스턴스를 반환하게 됩니다.
```java
    NutritionFacts cocaCola = new Builder(240, 8)
            .calories(100)
            .sodium(35)
            .carbohydrate(27)
            .build();
```
이렇게 되면 자기자신을 반환하기때문에 fluent API 혹은 메서드 체이닝이 가능해 집니다. 이렇게 되면 필수 속성에 해당하는 값을 설정을 강제하고 선택적인 속성들은 값을 설정해도 되고 안해도 되기때문에 자바빈즈 보다 훨씬 객체를 안전하게 만들 수 있습니다.

생성자의 매개변수도 줄고 Optional한 것들은 부가적으로 사용할 수 있고 결과적으로 생성자 방식과 자바빈즈 방식의 장점만을 취하는 방법입니다.

Builder패턴은 구현하게되면 Builder 클래스안에 똑같은 필드들이 중복되고 체이닝메서드를 구현해야 하기 떄문에 코드를 파악하기 힘들어집니다. 그렇지만 Lombok이라는 라이브러리를 통해서 상당량의 코드를 줄일 수 있습니다.

```java
@Builder
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;
 
 	//....
 }
```
이런식으로 위에 Builder어노테이션을 붙이면 컴파일시에 빌더를 알아서 만들어 줍니다.
<br>

```java
NutritionFacts nutritionFacts = new NutritionFactsBuilder()
		.servingSize(100)
        .servings(10)
		.build()
```
Builder에노테이션하나로 코드가 상당히 줄어들지만 단점은 모든 파라미터를 받는 생성자가 생겨서
```java
NutritionFacts nutritionFacts = new NutritionFacts(... 모든 인자값을 받을 수 있음)
```
이런식으로 객체 생성이 가능해집니다. 결국 생성자에 많은 매개변수들이 외부로 노출이 됩니다. 이런경우를 방지하고 싶다면

```java
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;
 
 	//....
 }
```
AllArgsConstructor어노테이션을 적용해 생성자의 접근을 private으로 설정하면 빌더로만 객체를 생성할 수 있게 됩니다.

하지만 극복하기 어려운 단점이 하나 있는데 필수값을 생성자의 매개변수로 지정할 수 없다는 점입니다.
```java
NutritionFacts cocaCola = new Builder(240, 8)
         .calories(100)
         .sodium(35)
         .carbohydrate(27)
         .build();
```
처음에 직접 빌더를 구현했을 때는 필수값을 생성자의 인자로 전달하고 선택적인 값들만 체이닝으로 설정 할 수 있었지만 @Builder를 사용하면 체이닝을 통해서만 값들을 설정해야 합니다.

```java
NutritionFacts nutritionFacts = new NutritionFactsBuilder()
		.servingSize(100)
        .servings(10)
		.build()
```

<br>
필수적인 필드가 있고 필수적이지 않은 필드가 있는데 이것때문에 생성자의 매개변수가 너무 많이 늘어날때, 그리고 Immutable한 객체를 만들고 싶을때 권장되는 방식입니다. 

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#
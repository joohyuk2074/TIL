# 타입과 추상화

## 추상화를 통한 복잡성 극복

추상화의 목적은 불필요한 부분을 무시함으로써 현실에 존재하는 복잡성을 극복하는 것이다.
<br>
이 책에서는 추상화를 다음과 같이 정의한다.

> **추상화** <br>
> 어떤 양상, 세부 사항, 구조를 좀 더 명확하게 이해하기 위해 특정 절차나 물체를 의도적으로 생략하거나 감춤으로써 복잡도를 극복하는 방법이다.
> 복잡성을 다루기 위해 추상화는 두 차원에서 이뤄진다.
>
> - 첫 번째 차원은 구체적인 사물들 간의 공통점은 취하고 차이점은 버리는 일반화를 통해 단순하게 만드는 것이다.
> - 두 번째 차원은 중요한 부분을 강조하기 위해 불필요한 세부 사항을 제거함으로써 단순하게 만드는 것이다. <br>
>
> 모든 경우에 추상화의 목적은 복잡성을 이해하기 쉬운 수준으로 단순호하는 것이라는 점을 기억하라.

<hr>

## 타입

### 1. 타입은 개념이다

타입은 공통점을 기반으로 객체들을 묶기 위한 틀이다.

> 타입은 개념과 동일하다. 따라서 타입이란 우리가 인식하고 있는 다양한 사물이나 객체에 적용할 수 있는 아이디어나 관념을 의미한다. 어떤 객체에 타입을 적용할 수 있을 때 그 객체를 타입의 인스턴스라고 한다. 타입의 인스턴스는 타입을 구성하는 외연인 객체 집합의 일원이 된다.

### 2. 데이터 타입

타입 시스템의 목적은 메모리 안의 모든 데이터가 비트열로 보임으로써 야기되는 혼란을 방지하는 것이다.
타입 시스템은 메모리 안에 저장된 0과 1에 대해 수행 가능한 작업과 불가능한 작업을 구분함으로써 데이터가 잘못 사용되는 것을 방지한다. 결과적으로 타입 시스템의 목적은 데이터가 잘못 사용되지 않도록 제약사항을 부과하는 것이다.
<br>
이를 통해 타입에 관련된 두 가지 중요한 사실을 알 수 있다.
<br>
첫째, 타입은 데이터가 어떻게 사용되느냐에 관한 것이다. 중요한 점은 연산자의 종류가 아니라 어떤 데이터에 어떤 연산자를 적용할 은 있느냐가 그 데이터의 타입을 결정한다.<br>
둘째, 타입에 속한 데이터를 메모리에 어떻게 표현하는지는 외부로부터 철저하게 감춰진다.
<br>

> **데이터 타입**은 메모리 안에 저장된 데이터의 종류를 분류하는 데 사용하는 메모리 집합에 관한 메타데이터다. 데이터에 대한 분류는 암시적으로 어떤 종류의 연산이 해당 데이터에 대해 수행될 수 있는지를 결정한다.

### 3. 객체와 타입

객체는 행위에 따라 변할 수 있는 상태를 가지고 있다는 사실을 기억하라.<br>
객체를 창조할 때 가장 중요하게 고려해야 하는 것은 객체가 이웃하는 객체와 협력하기 위해 어떤 행동을 해야 할지를 결정하는 것이다. 즉, 객체가 협력을 위해 어떤 책임을 지녀야 하는지를 결정하는 것이 객체지향 설계의 핵심이다.
<br>
따라서 앞에서 데이터 타입에 관해 언급했던 두 가지 조언은 객체의 타입을 이야기 할 때도 동일하게 적용된다.
<br>
첫째, 어떤 객체가 어떤 타입에 속하는지를 결정하는 것은 객체가 수행하는 행동이다.<br>
둘째, 객체의 내부적인 표현은 외부로부터 철저하게 감춰진다.

### 4. 행동이 우선이다

객체의 내부 표현 방식이 다르더라도 어떤 객체들이 동일하게 행동한다면 그 객체들은 동일한 타입에 속한다. 결과적으로 동일한 책임을 수행하는 일련의 객체는 동일한 타입에 속한다고 말할 수 있다. <br>
결론적으로 객체의 타입을 결정하는 것은 객체의 행동뿐이다. 객체가 어떤 데이터를 보유하고 있는지는 타입을 결정하는데 아무런 영향도 미치지 않는다.<br>
훌륭한 객체지향 설계는 외부에 행동만을 제공하고 데이터는 행동 뒤로 감춰야 한다. 이 원칙을 흔히 **캡슐화**라고 한다.
<br><br>
객체를 결정하는 것은 행동이다. 데이터는 단지 행동을 따를 뿐이다. 이것이 객체를 객체답게 만드는 가장 핵심적인 원칙이다.
많은 클래스가 하나 이상의 자원에 의존합니다. 사용하는 자원에 따라 동작이 달라지는 경우 의존 객체 주입을 고려할 수 있습니다.

```java
public class SpellChecker {

    private static final Dictionary dictionary = new DefaultDictionary();

    private SpellChecker() {}

    public static boolean isValid(String word) {
    	// .... 코드 생략
        return dictionary.contains(word);
    }

    public static List<String> suggestions(String typo) {
    	// .... 코드 생략
        return dictionary.closeWordsTo(typo);
    }
}
```
위의 SpellChecker는 spelling이 잘못됐는지 판단해주는 isValid메서드와 잘못됐을때 비슷한 단어를 추천해주는 suggestions메서드가 있습니다. 이 메서드들은 dictionary객체 를 의존해서 사용하고 있는데 SpellChecker내부에서 직접 생성자를 호출해서 사용하고 있습니다. 이런경우 isValid, suggestions메서드에 대한 테스트코드를 작성할때 dictionary를 반드시 사용해야하 하기 때문에 dictionary를 만드는 과정이 리소스를 많이 잡아먹는다면 비효율적인 테스트가 될 수 있습니다. 
#### SpellChecker의 isValid메서드 테스트
```java
public class SpellCheckerTest {

    @Test
    void isValid() {
        assertTrue(SpellChecker.isValid("test"));	// 항상 dictionary 인스턴스를 생성해야함
    }

}
```
<br>

또한 실전에서 dictionary는 영어사전, 독일어사전 등등 여러 사전으로 바꿔서 사용하고 싶을수 있는데 Dictionary를 의존하는 SpellChecker는 내부에서 인스턴스를 직접 생성하기때문에 각 언어에 대한 KoreanSpellChecker를 생성해서 한국어 사전의 인스턴스를 생성하여 사용해야합니다. 이렇게되면 반복적인 코드가 많아지기때문에 이런방법 보다는 Dictionary의 객체를 외부에서 주입받는 방식으로 변경하면 좀 더 유연하게 SpellChecker를 활용할 수 있습니다.

#### 외부주입을 받는 SpellChecker
```java
public class SpellChecker {

    private Dictionary dictionary;

    public SpellChecker(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public boolean isValid(String word) {
        // .... 코드 생략
        return dictionary.contains(word);
    }

    public List<String> suggestions(String typo) {
        // .... 코드 생략
        return dictionary.closeWordsTo(typo);
    }
}

```
이런식으로 생성자의 인자로 여러가지 Dictionary의 인스턴스를 받아올 수 있기때문에 SpellChecker의 코드들을 재사용 할 수 있습니다. isValid, suggestions를 테스트할때도 dictionary를 mocking할 수 있어 효율적인 단위테스트가 가능해집니다.

#### 테스트용 MockDictionary
```java
public class MockDictionary implements Dictionary{
    @Override
    public boolean contains(String word) {
        return false;
    }

    @Override
    public List<String> closeWordsTo(String typo) {
        return null;
    }
}
```

#### 테스트용 MockDictionary를 주입받은 테스트코드
```java
class SpellCheckerTest {

    @Test
    void isValid() {
        SpellChecker spellChecker = new SpellChecker(new MockDictionary());
        spellChecker.isValid("test");
    }

}
```

### 참고
- EffectiveJava(Joshua Bloch)
- https://www.inflearn.com/course/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-1#
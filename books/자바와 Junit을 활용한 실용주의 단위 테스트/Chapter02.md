# JUnit 진짜로 써 보기

## 2.1 테스트 대상 이해: Profile 클래스
여기서는 iloveyouboss 애플리케이션의 일부에 대한 테스트를 작성합니다. 이 프로그램은 잡코리아나 사람인 등과 경쟁하는 구직 웹 사이트입니다. 이 애플리케이션은 잠재적인 구인자에게 유망한 구직자를 매칭하고 데이트 웹 사이트가 그러하듯 반대 방향에 대한 서비스도 제공합니다.

구인자와 구직자는 둘 다 다수의 객관식 혹은 yes-no 질문에 대답을 하는 프로파일을 생성합니다. 웹 사이트는 다른 측 기준에 맞는 프로파일로 점수를 매기고, 고용주와 고용자 모두의 관점에서 최상의 매치를 보여줍니다.

iloveyouboss의 핵심 클래스인 Profile 코드
```java
package iloveyouboss;

import java.util.HashMap;
import java.util.Map;

public class Profile { 
   private Map<String, Answer> answers = new HashMap<>();
   private int score;
   private String name;

   public Profile(String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }

   public void add(Answer answer) {
      answers.put(answer.getQuestionText(), answer);
   }
   
   public boolean matches(Criteria criteria) {
      score = 0;
      
      boolean kill = false;
      boolean anyMatches = false; 
      for (Criterion criterion: criteria) {   
         Answer answer = answers.get(
               criterion.getAnswer().getQuestionText()); 
         boolean match = 
               criterion.getWeight() == Weight.DontCare ||
               answer.match(criterion.getAnswer());

         if (!match && criterion.getWeight() == Weight.MustMatch) {
            kill = true;
         }
         if (match) {         
            score += criterion.getWeight().getValue();
         }
         anyMatches |= match;  
      }
      if (kill)       
         return false;
      return anyMatches; 
   }

   public int score() {
      return score;
   }
}

```

Profile Test 코드
```java
package iloveyouboss;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileTest {

    private Profile profile;
    private BooleanQuestion question;
    private Criteria criteria;

    @BeforeEach
    public void create() {
        profile = new Profile("Bull Hockey, Inc.");
        question = new BooleanQuestion(1, "Got bonuses?");
        criteria = new Criteria();
    }

    @Test
    @DisplayName("matchAnswersFalseWhenWhenMustMatchCriteriaNotMet")
    public void matchAnswersFalseWhenWhenMustMatchCriteriaNotMet() {
        // given
        profile.add(new Answer(question, Bool.FALSE));
        criteria.add(new Criterion(new Answer(question, Bool.TRUE),Weight.MustMatch));

        // when
        boolean matches = profile.matches(criteria);

        // then
        assertFalse(matches);
    }

    @Test
    @DisplayName("matchAnswersTrueForAnyDontCareCriteria")
    public void matchAnswersTrueForAnyDontCareCriteria() {
        // given
        profile.add(new Answer(question, Bool.FALSE));
        criteria.add(new Criterion(new Answer(question, Bool.TRUE),Weight.DontCare));

        // when
        boolean matches = profile.matches(criteria);

        // then
        assertTrue(matches);
    }
}

```
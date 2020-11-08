# 좋은 테스트의 FIRST 속성

## 5.1 FIRST: 좋은 테스트 조건
- [F]ast: 빠른
- [I]solated: 고립된
- [R]epeatable: 반복 가능한
- [S]elf-validating: 스스로 검증 가능한
- [T]imely: 적시의

## 5.2 [F]IRST: 빠르다

```java
package iloveyouboss.domain;

import java.util.*;
import java.util.concurrent.atomic.*;

public class StatCompiler {
   static Question q1 = new BooleanQuestion("Tuition reimbursement?");
   static Question q2 = new BooleanQuestion("Relocation package?");

   class QuestionController {
      Question find(int id) {
         if (id == 1)
            return q1;
         else
            return q2;
      }
   }

   private QuestionController controller = new QuestionController();

   public Map<String, Map<Boolean, AtomicInteger>> responsesByQuestion(
         List<BooleanAnswer> answers) {
      Map<Integer, Map<Boolean, AtomicInteger>> responses = new HashMap<>();
      answers.stream().forEach(answer -> incrementHistogram(responses, answer));
      return convertHistogramIdsToText(responses);
   }

   private Map<String, Map<Boolean, AtomicInteger>> convertHistogramIdsToText(
         Map<Integer, Map<Boolean, AtomicInteger>> responses) {
      Map<String, Map<Boolean, AtomicInteger>> textResponses = new HashMap<>();
      responses.keySet().stream().forEach(id -> 
         textResponses.put(controller.find(id).getText(), responses.get(id)));
      return textResponses;
   }

   private void incrementHistogram(
         Map<Integer, Map<Boolean, AtomicInteger>> responses, 
         BooleanAnswer answer) {
      Map<Boolean, AtomicInteger> histogram = 
            getHistogram(responses, answer.getQuestionId());
      histogram.get(Boolean.valueOf(answer.getValue())).getAndIncrement();
   }

   private Map<Boolean, AtomicInteger> getHistogram(
         Map<Integer, Map<Boolean, AtomicInteger>> responses, int id) {
      Map<Boolean, AtomicInteger> histogram = null;
      if (responses.containsKey(id)) 
         histogram = responses.get(id);
      else {
         histogram = createNewHistogram();
         responses.put(id, histogram);
      }
      return histogram;
   }

   private Map<Boolean, AtomicInteger> createNewHistogram() {
      Map<Boolean, AtomicInteger> histogram;
      histogram = new HashMap<>();
      histogram.put(Boolean.FALSE, new AtomicInteger(0));
      histogram.put(Boolean.TRUE, new AtomicInteger(0));
      return histogram;
   }
}
```

QuestionController 객체의 find() 메서드를 호출하면 느린 영속적 저장소와 상호 작용합니다. 테스트가 느릴 뿐만 아니라 적절한 질문 개체를 얻어 오기 위해 데이터베이스도 실행해야 합니다. 질문을 얻기 위해 컨트롤러에 질의하기보다는 먼저 질문을 가져오고, 그 텍스트를 responsesByQuestion() 메서드의 인수로 넘깁니다.

```java
   public Map<Integer,String> questionText(List<BooleanAnswer> answers) {
      Map<Integer,String> questions = new HashMap<>();
      answers.stream().forEach(answer -> {
         if (!questions.containsKey(answer.getQuestionId()))
            questions.put(answer.getQuestionId(), 
               controller.find(answer.getQuestionId()).getText()); });
      return questions;
   }
```

responseByQuestion() 메서드에 질문 ID와 내용을 매핑하는 questions 변수를 추가합니다.

```java
   public Map<String, Map<Boolean, AtomicInteger>> responsesByQuestion(
         List<BooleanAnswer> answers, Map<Integer,String> questions) {
      Map<Integer, Map<Boolean, AtomicInteger>> responses = new HashMap<>();
      answers.stream().forEach(answer -> incrementHistogram(responses, answer));
      return convertHistogramIdsToText(responses, questions);
   }
```
responsesByQuestion() 메서드는 convertHistogramIdsToText() 메서드에 questions 맵을 넘깁니다.
```java
   private Map<String, Map<Boolean, AtomicInteger>> convertHistogramIdsToText(
         Map<Integer, Map<Boolean, AtomicInteger>> responses, 
         Map<Integer,String> questions) {
      Map<String, Map<Boolean, AtomicInteger>> textResponses = new HashMap<>();
      responses.keySet().stream().forEach(id -> 
         textResponses.put(questions.get(id), responses.get(id)));
      return textResponses;
   }
```
convertHistogramIdsToText() 메서드는 메모리상의 해시 맵만 사용하며 느린 영속적 저장소는 조회하지 않습니다. 이제 responsesByQuestion() 메서드를 손쉽게 테스트할 수 있습니다. 

```java
   @Test
   public void test() {
      StatCompiler stats = new StatCompiler();
      
      List<BooleanAnswer> answers = new ArrayList<>();
      answers.add(new BooleanAnswer(1, true));
      answers.add(new BooleanAnswer(1, true));
      answers.add(new BooleanAnswer(1, true));
      answers.add(new BooleanAnswer(1, false));
      answers.add(new BooleanAnswer(2, true));
      answers.add(new BooleanAnswer(2, true));
      
      Map<String, Map<Boolean,AtomicInteger>> responses = stats.responsesByQuestion(answers);
      
      assertThat(responses.get("Tuition reimbursement?").get(Boolean.TRUE).get(), equalTo(3));
      assertThat(responses.get("Tuition reimbursement?").get(Boolean.FALSE).get(), equalTo(1));
      assertThat(responses.get("Relocation package?").get(Boolean.TRUE).get(), equalTo(2));
      assertThat(responses.get("Relocation package?").get(Boolean.FALSE).get(), equalTo(0));
   }
```
> 코드를 클린 객체 지향 설계 개념과 맞출수록 단위 테스트 작성도 쉬워집니다.

<hr>

## 5.3 F[I]RST: 고립시킨다

- 좋은 단위 테스트는 검증하려는 작은 양의 코드에 집중합니다. 직접적 혹은 간접적으로 테스트 코드와 상호 작용하는 코드가 많을수록 문제가 발생할 소지가 늘어납니다.
- 좋은 단위 테스트는 다른 단위 테스트에 의존하지 않습니다.
- 테스트 코드는 어떤 순서나 시간에 관계없이 실행할 수 있어야 합니다.
- 각 테스트가 작은 양의 동작에만 집중하면 테스트 코드를 집중적이고 독립적으로 유지하기 쉬워집니다.

<hr>

## 5.4 FI[R]ST: 좋은 테스트는 반복 가능해야 한다
- 반복 가능한 테스트는 실행할 때마다 결과가 같아야 합니다. 따라서 반복 가능한 테스트를 만들려면 직접 통제할 수 없는 외부 환경에 있는 항목들과 격리시켜야 합니다.

## 5.5 FIR[S]T: 스스로 검증 가능하다

## 5.6 FIRS[T]: 적시에 사용한다


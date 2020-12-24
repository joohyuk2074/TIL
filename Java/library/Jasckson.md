# Jackson ObjectMapper 소개
Jackson 라이브러리의 ObjectMapper에 대해 공부하면서 [여기](https://www.baeldung.com/jackson-object-mapper-tutorial)에 있는 원문을 한글로 해석해봤습니다.

## 1. 개요
이 튜토리얼의 목표는 Jackson라이브러리의 ObjectMapper 클래스에 대한 이해와 어떻게 직렬화와 역직렬화를 할 수 있는지에 대해 포커스를 두고 있습니다.

## 2. Dependencies
스프링 부트는 org.springframework.boot:spring-boot-starter-web에 기본적으로 포함된 jackson 라이브러리를 사용합니다.

## 3. ObjecgtMapper를 사용하여 읽고 쓰기
우선 ObjectMapper의 readValue에 대해 알아보겠습니다. 이 API를 사용하여 JSON을 분석하거나 역직렬화 할 수 있습니다. 또한 쓰기 측면에서 writeValue API를 사용하여 모든 Java 객체를 JSON으로 직렬화 할 수 있습니다.

<br>

직렬화와 역직렬화를 실습하기 위해 아래와 같은 Car 클래스를 사용하겠습니다.
```java
public class Car {
    private String color;
    private String type;

    // 기본적인 getter, setter
}
```

### 3.1 자바 객체를 JSON으로 
ObjectMapper 클래스의 writeValue 메소드를 사용하여 Java 객체를 JSON으로 직렬화하는 첫 번째 예를 살펴보겠습니다.

```java
ObjectMapper objectMapper = new ObjectMapper();
Car car = new Car("yellow", "renault");
objectMapper.writeValue(new File("target/car.json"), car);
```

결과는 target/car.json에
```json
{"color":"yellow","type":"renault"}
```
이런식으로 나오게 됩니다.

<br>

ObjectMapper의 writeValueAsString과 writeValueAsBytes 메소드는 Java 객체에서 JSON을 생성하고 JSON을 Java 객체로 반환합니다.

```java
String carAsString = objectMapper.writeValueAsString(car);
```

### 3.2 Json을 자바 객체로
아래 예제는 Json 문자열을 자바객체로 바꿔줍니다. 

```java
String json = "{ \"color\" : \"Black/", \"type\" : \"BMW\" }"
Car car = objectMapper.readValue(json, Car.class);
```

readValue 메서드는 Json 문자열이 있는 파일도 받을 수 있다.
```java
Car car = objectMapper.readValue(new File("src/test/resources/json_car.json"), Car.class);

Car car = objectMapper.readValue(new URL("file:src/test/resources/json_car.json"), Car.class);
```

### 3.3 JSON을 JsonNode로
또한 Json은 JsonNode객체로 파싱될 수 있고 특정 노드에서 데이터를 검색하는데 사용될 수 있습니다.

```java
String json =  "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
JsonNode jsonNode = objectMapper.readTree(json);
String color = jsonNode.get("color").asText();
// 결과: color -> Black
```

### 3.4 Json의 배열을 Java의 리스트로
TypeReference를 이용하여 배열의 형태인 JSON을 자바의 리스트로 변환할 수 있습니다.

```java
String jsonCarArray = 
  "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
```

### 3.5 Json 문자열을 자바 Map으로 변환
비슷하게 Json을 자바 Map으로 파싱할 수 있습니다.

```java
String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
Map<Strng, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
```

# 4. 고급 기능 
Jackson 라이브러리의 강점은 사용자가 커스텀하게 설정하여 직렬화와 역직렬화를 수행할 수 있습니다.

## 4.1 직렬화 혹은 역직렬화 설정
Json 객체를 Java 클래스로 변환하는 동안 Json 문자열에 새 필드가 있는 경우 기본 프로세스에서 예외가 발생합니다.

```java
String jsonString 
  = "{ \"color\" : \"Black\", \"type\" : \"Fiat\", \"year\" : \"1970\" }";
```

위의 JSON문자열을 자바 객체의 Car 클래스로 파싱하면 year이라는 필드가 없기 때문에 UnrecognizedPropertyException예외를 발생시킵니다.

<br>

하지만 설정을 통해 새 필드를 무시하도록 기본 프로세스를 확장 할 수 있습니다.

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOW_PROPERTIES, false);
Car car = objectMapper.readValue(jsonString, Car.class);

JsonNode jsonNodeRoot = objectMapper.readTree(jsonString);
JsonNode jsonNodeYear = jsonNodeRoot.get("year");
String year = jsonNodeYear.asText();
```

또 다른 옵션은 FAIL_ON_NULL_FOR_PRIMITIVES를 기반으로하며, 기본 값에 대해 null 값이 허용되는지 여부를 정의합니다.

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
```

마찬가지로 FAIL_ON_NUMBERS_FOR_ENUM은 열거형 값을 숫자로 직렬화 / 역직렬화 할 수 있는지 여부를 제어합니다.

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
```

## 4.2 사용자지정 Serializer, Deserializer 생성
objectMapper 클래스의 또 다른 필수 기능은 사용자 지정 serializer 및 deserializer를 등록하는 기능입니다.

사용자 지정의 serializer, deserializer는 입력 또는 출력 JSON 응답이 직렬화, 역직렬화되어야하는 Java 클래스와 구조가 다른 상황에서 매우 유용합니다.

```java
public class CustomCarSerializer extends StdSerializer<Car> {

    public CustomCarSerializer() {
        this(null);
    }

    public CustomCarSerializer(Class<Car> t) {
        super(t);
    }

    @Override
    public void serialize(Car car, JsonGenerator jsonGenerator, SerializerProvider serializer) {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStartField("car_brand", car.getType());
        jsonGenerator.writeStartObject();
    }
}
```

이 사용자지정 Serializer는 아래와 같이 호출될 수 있습니다.

```java
ObjectMapper mapper = new ObjectMapper();
SimpleModule module = new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
module.addSerializer(Car.class, new CustomCarSerializer());
mapper.registerModule(module);
Car car = new Car("yellow", "renault");
String carJson = mapper.writeValueAsString(car);
```

다음은 클라이언트 측에서 Car의 모습입니다.

```javascript
var carJson = {"car_brand":"renault"}
```

참고자료: https://www.baeldung.com/jackson-object-mapper-tutorial

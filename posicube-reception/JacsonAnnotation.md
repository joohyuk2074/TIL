## @JsonInclude
- 어노테이션 속성을 제외 하는데 사용 된다.
```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public static class MyBean {
    public int id;
    public String name;
}
```
```json
// NON_NULL 사용시 namedl null인 경우에 제외 됩니다.
"id" : 1
```

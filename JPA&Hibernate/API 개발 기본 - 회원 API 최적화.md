# API 개발 기본

## 회원 등록 API

```java
package me.weekbelt.jpashop.api;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;


    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}

```

### V1 엔티티를 Request Body에 직접 매핑

- 문제점
  - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
  - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
  - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
  - 엔티티가 변경되면 API 스펙이 변한다.
- 결론
  - API 요청스펙에 맞추어 별도의 DTO를 파라미터로 받는다.

### V2 엔티티 대신에 DTO를 RequestBody에 매핑

- CreateMemberRequest를 Member엔티티 대신에 RequestBody와 매핑한다.
- 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
- 엔티티와 API 스펙을 명확하게 분리할 수 있다.
- 엔티티가 변해도 API의 스펙은 변하지 않는다.

  참고 : 실무에서는 엔티티를 API 스펙에 노출하면 안된다!

<hr>

## 회원 수정 API

```java
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    class UpdateMemberResponse {
        private Long id;
        private String name;
    }
```

- 회원 수정도 DTO를 요청파라미터에 매핑

```java
package me.weekbelt.jpashop.service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    /**
     * 회원수정
     */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}

```

- 변경 감지를 사용해서 데이터를 수정

<hr>

## 회원 조회 API

### 회원조회 V1: 응답 값으로 엔티티를 직접 노출

```java
package me.weekbelt.jpashop.api;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

}
```

- 문제점
  - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
  - 기본적으로 엔티티의 모든 값이 노출된다.
  - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
  - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
  - 엔티티가 변경되면 API 스펙이 변한다.
  - 추가로 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으로 해결)
- 결론

  - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.

  참고: 엔티티를 외부에 노출하면 안된다.
  실무에서는 member엔티티의 데이터가 필요한 API가 계속 증가하게 된다. 어떤 API는 name필드가 필요하지만, 어떤 API는 name필드가 필요없을 수 있다. 결론적으로 엔티티 대신에 API 스펙에 맞는 별도의 DTO를 노출해야 한다.

### 회원조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO 사용

```java
package me.weekbelt.jpashop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.weekbelt.jpashop.domain.Member;
import me.weekbelt.jpashop.service.MemberService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환한다.
     */
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    class MemberDto {
        private String name;
    }

}

```

- 엔티티를 DTO로 변환해서 반환한다.
- 엔티티가 변해도 API 스펙이 변경되지 않는다.
- 추가로 Result 클래스로 컬렉션을 감싸서 향후 필요한 필드를 추가할 수 있다.

<hr>

## 간단한 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화

OrderSimpleApiController.java

```java
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
        return result;
    }
```

OrderRepository.java

```java
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .getResultList();
    }
```

- 엔티티를 페치 조인(fetch join)을 사용해서 쿼리 1번에 조회
- 페치 조인으로 order -> member, order -> delivery는 이미 조회 된 상태 이므로 지연로딩 x

<hr>

## 간단한 주문 조회 V4: JPA에서 DTO로 바로 조회

OrderSimpleApiController.java

```java
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
```

### OrderSimpleQueryRepository.java 조회 전용 리포지토리

```java
package me.weekbelt.jpashop.repository.order.simplequery;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new me.weekbelt.jpashop.repository.order.simplequery.OrderSimpleQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
```

### OrderSimpleQueryDto 리포지토리에서 DTO 직접 조회

```java
package me.weekbelt.jpashop.repository.order.simplequery;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime
            orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
```

- 일반적인 SQL을 사용할 때 처럼 원하는 값을 선택해서 조회
- new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
- SELECT 절에서 원하는 데이터를 직접 선택하므로 DB -> 애플리케이션 네트웍 용량 최적화(생각보다 미비하다)
- 리포지토리 재사용성이 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점

<hr>

### 정리

엔티티를 DTO로 변환하거나, DTO로 바로 조회하는 두가지 방법은 각각 장단점이 있다. 둘중 상황에 따라서 더 나은 방법을 선택하면 된다. 엔티티로 조회하면 리포지토리 재사용성도 좋고, 개발도 단순해진다. 따라서 권장하는 방법은 다음과 같다.

### 쿼리 방식 선택 권장 순서

1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
2. 필요하면 페치 조인으로 성능을 최적화 한다. -> 대부분의 성능 이슈가 해결된다.
3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.

참고 강의:[실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-API%EA%B0%9C%EB%B0%9C-%EC%84%B1%EB%8A%A5%EC%B5%9C%EC%A0%81%ED%99%94/dashboard)

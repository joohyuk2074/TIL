# API 개발 고급 - 컬렉션 조회 최적화

주문내역에서 추가로 주문한 상품 정보를 추가로 조회하자.<br>
Order 기준으로 컬렉션인 OrderItem와 Item이 필요 하다.<br><br>

## 주문 조회 V1: 엔티티 직접 노출

```java
package me.weekbelt.jpashop.api;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll();
        for (Order order : all) {
            order.getMember().getName();        // Lazy 강제 초기화
            order.getDelivery().getAddress();   // Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());  // Lazy 강제 초기화
        }
        return all;
    }
}

```

- orderItem, item 관계를 직접 초기화하면 Hibernate5Module 설정에 의해 엔티티를 JSON으로 생성 한다.
- 양방향 연관관계면 무한 루프에 걸리지 않게 한곳에 @JsonIgnore를 추가해야 한다.
- 엔티티를 직접 노출하므로 좋은 방법은 아니다.

<hr>

## 주문 조회 V2: 엔티티를 DTO로 변환

```java
package me.weekbelt.jpashop.api;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(toList());

        return result;
    }


    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;//상품 명
        private int orderPrice; //주문 가격
        private int count; //주문 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
```

- 지연 로딩으로 너무 많은 SQL 실행
- SQL 실행 수
  - order 1번
  - member, address N번(order 조회 수 만큼)
  - orderItem N번(order 조회 수 만큼)
  - item N번(orderItem 조회 수 만큼)

> 참고: 지연 로딩은 영속성 컨텍스트에 있으면 영속성 컨텍스트에 있는 엔티티를 사용하고 없으면 SQL을 실행한다. 따라서 같은 영속성 컨텍스트에서 이미 로딩한 회원 엔티티를 추가로 조회하면 SQL을 실행하지 않는다.

<hr>

## 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화

```java
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(toList());

        return result;
    }
```

### OrderRepository에 추가

```java
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }
```

- 페치 조인으로 SQL이 1번만 실행됨
- distinct를 사용한 이유는 1대다 조인이 있으므로 데이터베이스 row가 증가한다. 그 결과 같은 order엔티티의 조회 수도 증가하게 된다. JPA의 distinct는 SQL에 distinct를 추가하고, 더해서 같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다. 이 예에서 order가 컬렉션 페치 조인 때문에 중복 조회 되는 것을 막아준다.
- 단점
  - 페이징 불가능

> 참고: 컬렉션 페치 조인을 사용하면 페이징이 불가능하다. 하이버네이트는 경고 로그를 남기면서 모든 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다.(매우 위험하다.)

> 참고: 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이상에 페치 조인을 사용하면 안된다. 데이터가 부정합하게 조회될 수 있다.

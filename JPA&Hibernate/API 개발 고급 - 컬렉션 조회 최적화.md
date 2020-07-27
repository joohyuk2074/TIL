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

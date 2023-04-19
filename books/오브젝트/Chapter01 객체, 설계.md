# 1. 티켓 판매 애플리케이션 구현하기

초대일자가 포함된 초대장
```java
public class Invitation {
    
    private LocalDateTime when;
}

```

공연을 관라하기 위해 필요한 Ticket
```java
public class Ticket {
    
    private Long fee;

    public Long getFee() {
        return fee;
    }
}
```

이벤트에 당첨된 사람은 티켓으로 교환할 초대장을 가지고 있고 당첨되지 않은 관람객은 티켓을 구매할 수 있는 현금을 보유하고 있을 것이다. 관람객은 오직 초대장, 현급, 티켓 세 가지만 가져올 수 있고 그 것들을 담고 있는 Bag 클래스를 추가한다.
```java
public class Bag {

    private Long amount;

    private Invitation invitation;

    private Ticket ticket;


    public Bag(Long amount) {
        this(null, amount);
    }

    public Bag(Invitation invitation, long amount) {
        this.invitation = invitation;
        this.amount = amount;
    }


    public boolean hadInvitation() {
        return invitation != null;
    }

    public boolean hasTicket() {
        return ticket != null;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}

```

Bag을 소지하고 있는 관람객이라는 개념을 구현하는 Audience 클래스 생성
```java
public class Audience {

    private Bag bag;

    public Audience(Bag bag) {
        this.bag = bag;
    }

    public Bag getBag() {
        return bag;
    }
}

```

관람객에게 판매할 티켓과 티켓의 판매 금액이 보관돼 있는 TicketOffice 클래스 생성
```java
public class TicketOffice {

    private Long amount;
    private List<Ticket> tickets = new ArrayList<>();

    public TicketOffice(Long amount, Ticket... tickets) {
        this.amount = amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }

    public Ticket getTicket() {
        return tickets.get(0);
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}
```

초대장을 티켓으로 교환해 주거나 티켓을 판매하는 역할을 수행하는 TicketSeller 클래스 생성
```java
public class TicketSeller {

    private TicketOffice ticketOffice;

    public TicketSeller(TicketOffice ticketOffice) {
        this.ticketOffice = ticketOffice;
    }

    public TicketOffice getTicketOffice() {
        return ticketOffice;
    }
}
```
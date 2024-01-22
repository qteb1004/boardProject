package com.example.boardProject.entity;

import com.example.boardProject.constant.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_id") // 정렬할 때 사용하는 "order" 키워드가 있기 떄문에 Order엔티티에 매핑되는 테이블로 지정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    // 한명의 회원은 여러 번 주문할 수 있기 떄문에 다대일 단방향 매핑

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY) // 영속성전이 옵션 설정ALL, 고아객체제거true
    private List<OrderItem> orderItems = new ArrayList<>(); // 하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해 매핑한다.
    /* 주문 상품 엔티티와 일대다 매핑. 외래키(order_id) 가  order_item 테이블에 있으므로 연관 관계의 주인은
     OrderItem 엔티티이다. Order 엔티티가 주인이 아니므로 "mappedBy" 속성으로 연관 관계의 주인을 설정한다.
      속성의 값으로 "order"를 적어준 이유는 OrderItem에 있는 Order에 의해 관리된다는 의미이다.
      즉 연관 관계의 주인의 필드인 order를 mappedBy의 값으로 셋팅하면 된다. */

}

// 무조건 양방향으로 연관관계를 매핑하면 해당 엔티티는 많은 테이블과 연관관계를 맺게 되고 엔티티 클래스 자체가
// 복잡해 지기 때문에 연관관계 단방향 매핑으로 설계 후 나중에 필요할 경우 양방향 매핑을 추가하는것을 권장.

package com.example.boardProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 하나의 상품은 여러 주문 상품으로 들어갈 수 있으므로 다대일 단방향 매핑
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY) // 한번의 주문에 여러 개의 상품을 주문할 수 있으므로 주문상품 엔티티와 주문 엔티티를 다대일 단방향 매핑
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문가격

    private int count; // 수량

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

}
// 다대일과 일대다는 반대 관계
// 주문 상품 엔티티 기준에서 다대일 매핑이었으므로 주문 엔티티 기준에서는 주문상품 엔티티와
// 일대다 관계로 매핑하면 된다. 또한 양방향 매핑에서는 '연관관계주인'을 설정해야 한다는 점이 중요하다.
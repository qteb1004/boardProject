package com.example.boardProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart")
public class Cart {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 회원 엔티티와 일대일로 매핑한다.
    @JoinColumn(name="member_id") // 매핑할 외래키 지정. name을 명시하지않아도 JPA가 알아서 ID를 찾지만 컬럼명이 원하는대로 생성되지 않을수 있기때문에 직접 지정한다.
    private Member member;
    // 이렇게 매핑을 맺어주면 장바구니엔티티를 조회하면서 회원 엔티티의 정보도 동시에 가져올 수 있다.


}
/* Member(회원) 엔티티를 보면 Cart(장바구니) 엔티티와 관련된 소스가 전혀 없다.
즉, 장바구니 엔티티가 일방적으로 회원 엔티티를 참조하고 있다.
장바구니와 회원은 일대일로 매핑돼 있으며, 장바구니 엔티티가 회원 엔티티를 참조하는 일대일 단방향 매핑이다.
 */
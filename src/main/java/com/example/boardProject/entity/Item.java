package com.example.boardProject.entity;

import com.example.boardProject.constant.ItemSellStatus;
import com.example.boardProject.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "item") // 엔티티인 item 클래스를 어떤 테이블과 매칭될것인지 지정. item 테이블과 매핑
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id") // 매핑될 컬럼의 이름설정 item 클래스의 id 변수와 item 테이블의 item_id 컬럼이 매핑
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // 상품코드

    @Column(nullable = false, length = 50)
    private String itemNm; // 상품명

    @Column(name = "price" , nullable = false)
    private int price; // 가격

    @Column(nullable = false)
    private int stockNumber; // 재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품판매상태 : 재고가 없거나, 상품을 미리 등록해 놓고 나중에 '판매 중' 상태로 바꾸거나 재고가 없을때는 프론트에 노출시키지 않기위함...

    // 상품 데이터 업데이트 로직...(비즈니스로직추가로 객체지향적으로 코딩할수 있게 됨)
    // 코드재활용/데이터변경포인트를 한군데에서 관리할 수 있다.
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

}

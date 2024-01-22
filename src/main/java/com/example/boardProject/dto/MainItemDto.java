package com.example.boardProject.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MainItemDto { // 메인페이지에서 상품 보여줄 때 사용
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    @QueryProjection // 생성자에 선언하여 Querydsl로 결과 조회시 MainItemDto 객체로 바로 받아올 수 있다.
    public MainItemDto(Long id, String itemNm, String itemDetail,
                       String imgUrl, Integer price){
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}

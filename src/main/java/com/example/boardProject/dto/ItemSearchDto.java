package com.example.boardProject.dto;

import com.example.boardProject.constant.ItemSellStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSearchDto { // 상품 데이터 조회 시 상품 조회조건 가지고있는 dto
    private String searchDateType; // 현재 시간과 상품 등록일을 비교해서 상품데이터 조회
    private ItemSellStatus searchSellStatus; // 상품의 판매상태를 기준으로 상품데이터 조회
    private String searchBy; // 상품을 조회할 때 어떤 유형으로 조회할지 선택
    private String searchQuery = ""; // 조회할 검색어 저장변수. searchBy가 itemNm일 경우 상품명기준, createdBy일 경우 상품 등록자 아이디 기준으로 검색
}

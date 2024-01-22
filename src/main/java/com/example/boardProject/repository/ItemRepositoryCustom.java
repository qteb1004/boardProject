package com.example.boardProject.repository;

import com.example.boardProject.dto.ItemSearchDto;
import com.example.boardProject.dto.MainItemDto;
import com.example.boardProject.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom { // Querydsl 을 JPA 와 함께 사용하기 위한 리포지토리
    // Querydsl : 조건에 맞는 쿼리를 동적으로 쉽게 생성할 수 있다. 비슷한 쿼리 재활용가능, 오류수정 용이
    // 1. 사용자 정의 인터페이스 작성
    // 2. 사용자 정의 인터페이스 구현
    // 3. Spring Data Jpa 리포지토리에서 사용자 정의 인터페이스 상속

    Page<Item> getUserItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    //상품조회 조건을 담고있는 itemSearchDto 객체와 페이징 정보를 담고있는 pageable 객체를 파라미터로
    // 받는 getUserItemPage 메소드를 정의. 반환 데이터로 Page<Item> 객체를 반환한다.


    // 메인 페이지에 보여줄 상품리스트 가져오기
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,
                                      Pageable pageable);
}

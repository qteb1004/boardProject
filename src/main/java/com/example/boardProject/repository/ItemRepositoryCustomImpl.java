package com.example.boardProject.repository;

import com.example.boardProject.constant.ItemSellStatus;
import com.example.boardProject.dto.ItemSearchDto;
import com.example.boardProject.dto.MainItemDto;
import com.example.boardProject.dto.QMainItemDto;
import com.example.boardProject.entity.Item;
import com.example.boardProject.entity.QItem;
import com.example.boardProject.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    // ItemRepositoryCustom 인터페이스 구현. 클래스명 끝에 Impl 붙여줘야 정상동작
    // BooleanExpression 이라는 where 절에서 사용할 수 있는 값 지원
    // 중복코드를 줄일 수 있다.

    private JPAQueryFactory queryFactory; // 동적으로 쿼리를 생성할수 있게된다.

    public ItemRepositoryCustomImpl(EntityManager em){ // JPAQueryFactory 의 생성자로 EntityManager 넣어줌
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
        // 상품 판매 상태 조건이 전체(null) 일 경우는 null 리턴
        // 결과값이 null 이면 where 절에서 해당 조건은 무시
        // 상품 판매 조건이 null 이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회한다.
    }
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null ){
            return null;
        } else if (StringUtils.equals("1d", searchDateType )) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
        // searchDateType 의 값에 따라서 dateTime 의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회
        // 예를 들어 searchDateType 의 값이 "1m" 인 경우 dateTime 의 시간을 한달 전으로 세팅 후 최근 한 달 동안 등록된 상품만 조회하도록 조건값을 반환한다.
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if (StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createBy", searchBy)){
            return QItem.item.createdBy.like("%"+ searchQuery + "%");
        }
        return null;
        // searchBy 의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어를 포함하고 있는
        // 상품을 조회하도록 조건값을 반환한다.
    }

    @Override
    public Page<Item> getUserItemPage(ItemSearchDto itemSearchDto,
                                      Pageable pageable){
        QueryResults<Item> results = queryFactory // 팩토리로 쿼리생성. 쿼리문을 직접 작성할 때의 형태와 문법이 비슷하다.
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
        // 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 반환한다.

    }




    // 검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ?
                null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,
                                             Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto( // QMainItemDto 생성자에 반환할 값 넣어줌. @QueryProjection 사용하면 DTO 로 바로 조회가 가능. 엔티티 조회 후 DTO로 변환하는 과정 줄일수 있음
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item) // itemImg 와 item 을 내부 조인
                .where(itemImg.repImgYn.eq("Y")) // 상품 이미지의 경우 대표 상품 이미지만 불러온다.
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}

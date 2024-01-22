package com.example.boardProject.repository;

import com.example.boardProject.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    // Querydsl 로 구현한 상품관리페이지 목록을 불러오는 getUserItemPage() 메소드 사용 가능
    List<Item> findByItemNm(String itemNm);
    // 상품명으로 데이터를 조회. by뒤에 필드명인 ItemName을 메소드의 이름에 붙여준다. 엔티티 명은 생략가능하므로
    // findItemByItemName 으로 쓰지 않아도 된다. 매개 변수로는 검색할 때 사용할 상품명 변수를 넘겨준다.

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    // 상품을 상품명과 상품상세설명을 OR 조건을 이용해 조회하는 쿼리 메소드

    List<Item> findByPriceLessThan(Integer price);
    // 파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터 조회

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    // 상품의 가격이 높은 순으로 조회

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc") // JPQL 로 작성한 쿼리문을 넣어준다. form 뒤에는 엔티티 클래스로 작성한 item 을 지정 /  item 으로 부터 데이터를 select 하겠다는 것 의미
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
    // @Param 을 이용해 파라미터로 넘어온 값을 JPQL 에 들어갈 변수로 지정해줄 수 있다.
    // 현재는 itemDetail 변수를 "like %%" 사이에 ":itemDetail" 로 값이 들어가도록 작성했다.
    // "테스트상품상세설명" 을 포함하고 있는 상품 데이터를 가격이 높은 순부터 조회

    // 복잡한 쿼리의 경우 @Query 어노테이션을 사용해 조회.......근데 난 왜 이게 더 어렵냐..

    // 만약 기존 데이터베이스에서 사용하던 쿼리를 그대로 사용해야할때는 @Query의 nativeQuery 속성을 사용하자
    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc",
    nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail")String itemDetail);

}

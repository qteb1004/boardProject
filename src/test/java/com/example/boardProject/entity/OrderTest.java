package com.example.boardProject.entity;

import com.example.boardProject.constant.ItemSellStatus;
import com.example.boardProject.repository.ItemRepository;
import com.example.boardProject.repository.MemberRepository;
import com.example.boardProject.repository.OrderItemRepository;
import com.example.boardProject.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderTest {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @Transactional
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {

        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            // 아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order 엔티티에 담는다.
        }

        orderRepository.saveAndFlush(order); // order엔티티를 저장하면서 강제로 flush 호출 해 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트의 상태 초기화

        Order savedOrder = orderRepository.findById(order.getId()) // 초기화했기떄문에 데이터베이스에서 주문 엔티티 조회. select쿼리문이 실행된다.
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
        // itemOrder 엔티티 3개가 실제로 데이터베이스에 저장되었는지 검사한다.

        // 영속성이 전이되면서 order에 담아 두었던 orderItem 이 insert 된다.
        // 3개의 orderItem을 담아두었으므로 3번의 insert 쿼리문이 실행된다.
    }

    public Order createOrder() { // 주문 데이터 생성하고 저장하는 메서드
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest() {
        Order order = this.createOrder();
        order.getOrderItems().remove(0); // order엔티티에서 관리하고 있는 orderItem 리스트의 0번째 인덱스 요ㅗ소 제거
        em.flush();
    }

    @Autowired
    OrderItemRepository orderItemRepository;

    /*@Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder(); // 기존에 만들었던 주문 생성 메소드를 이용하여 주문 데이터를 저장한다.
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId) // 영속성상태 초기화 후 order엔티티에 저장했던 주문 상품 아이디를 이용하여 orderItem 을 데이터베이스에서 다시 조회한다.
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class : " + orderItem.getOrder().getClass());
        // orderItem 엔티티에 있는 order 객체의 클래스를 출력한다.
        // Order 클래스가 출력되는 것을 확인할 수 있다.
        // 출력 결과 : Order class : class com.shop.entity.Order

        // orderItem 엔티티 하나를 조회했는데 엄청 긴 쿼리문이 나온다.
        // order_item 테이블과 item, orders, member 테이블을 조인해서 한꺼번에 가져오기 때문
    } */

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class : " + orderItem.getOrder().getClass()); // 1
        System.out.println("==========================");
        orderItem.getOrder().getOrderDate(); // 2
        System.out.println("==========================");
        // 1코드 실행 결과 Order 클래스 조회 결과가 HibernateProxy 라고 출력되는데 지연로딩으로 설정하면
        // 실제 엔티티 대신에 프록시 객체를 넣어둔다. 프록시 객체는 사용되기 전까지 데이터 로딩을 하지않고
        // 실제 사용 시점에 조회쿼리문이 실행된다

        // 2코드에서 Order 의 주문일(orderDate)을 조회할 때 select 쿼리문이 실행되는데
        // 디버깅 모드로 실행 후 코드를 한 줄씩 실행해보면 쉽게 이해할 수 있다.
    }
}
package com.example.boardProject.service;

import com.example.boardProject.dto.ItemFormDto;
import com.example.boardProject.dto.ItemImgDto;
import com.example.boardProject.dto.ItemSearchDto;
import com.example.boardProject.dto.MainItemDto;
import com.example.boardProject.entity.Item;
import com.example.boardProject.entity.ItemImg;
import com.example.boardProject.repository.ItemImgRepository;
import com.example.boardProject.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemImgService itemImgService;
    @Autowired
    ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto,
                         List<MultipartFile> itemImgFileList) throws Exception{
        // ItemFormDto 와 이미지 파일리스트를 파라미터로 받는다.
        // 상품등록. 반환값으로 저장된 상품의 ID 를 Long 형태로 반환한다.

        Item item = itemFormDto.createItem(); // 상품 등록 폼으로부터 입력받은 데이터를 이용하여 item 객체 생성
        itemRepository.save(item); // 상품 데이터를 저장

        // 이미지등록
        for (int i = 0 ; i< itemImgFileList.size(); i++ ){ // 이미지파일 리스트를 순회하며 각 이미지 처리하는 루프
            ItemImg itemImg = new ItemImg(); // 상품의 이미지 정보를 나타내는 ItemImg 객체 생성
            itemImg.setItem(item); // 생성 한 Item 객체인 item 을 itemImg 에 설정하여 해당 이미지가 어떤 상품에 속하는지 표시
            if(i==0) // 현재 이미지가 리스트에서 첫번째 이미지인지 확인한다.
                // 첫번째 이미지일 경우 대표상품 이미지 여부값을 "Y"로 셋팅 / 나머지 상품 이미지는 "N" 으로 설정
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i)); // 상품 이미지 정보 저장
            // 상품이미지와 이미지파일을 받아서 데이터베이스에 저장하는 역할수행
        }
        return item.getId(); // 저장된 상품의 ID 반환
        // 상품정보와 상품 이미지 정보를 데이터베이스에 저장하는 역할
        // 상품의 이미지중 첫번째 이미지를 대표 이미지로 설정, 나머지 이미지들은 대표이미지가 아님을 표시하는 메서드이다.
    }



    // 등록된 상품 불러오는 메서드
    @Transactional(readOnly = true) // 상품데이터를 읽어오는 트랜젝션을 읽기전용설정 JPA가 변경감지를 수행하지 않아서 성능향상
    public ItemFormDto getItemDtl(Long itemId) { // 주어진 itemId 로 특정 상품의 세부정보를 가져오는 메서드

        List<ItemImg> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId); // 주어진 itemId 로 해당상품에 속하는 이미지들을 상품이미지아이디의 오름차순으로 조회
        // 등록 순으로 가지고 오기 위해 상품 이미지 아이디 오름차순으로 가지고옴
        List<ItemImgDto> itemImgDtoList = new ArrayList<>(); // ItemImgDto 객체를 담을 리스트 생성
        for (ItemImg itemImg : itemImgList) { // 조회한 상품 이미지리스트를 순회하며 각 이미지 정보를 처리하는 루프
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg); // ItemImgDto 의 정적 메서드인 of 를 사용하여 itemImg 객체를 변환해 itemImgDto 객체로 만든다.
            itemImgDtoList.add(itemImgDto); // 생성한 itemImgDto 객체를 리스트에 추가
        }
        Item item = itemRepository.findById(itemId) // 상품아이디를 통해 상품엔티티조회. 존재하지 않을땐 ENFE 발생
                .orElseThrow(EntityNotFoundException::new);
        // 주어진 itemId를 사용하여 상품 엔티티를 조회한다.
        // 만약 해당 ID로 상품이 존재하지 않는다면, EntityNot어쩌고를 발생시킨다.

        ItemFormDto itemFormDto = ItemFormDto.of(item); // 조회한 item 객체를 사용해 ItemFormDto 생성
        itemFormDto.setItemImgDtoList(itemImgDtoList); // 생성한 DTO 리스트를 itemFormDto 에 설정
        return itemFormDto;
        // 주어진 상품 ID를 사용하여 상품의 세부 정보와 관련된 이미지 정보를 조회하고, 해당정보를 ItemFormDto 객체에 담아 반환한다.
    }





    // 상품업데이트 시 변경감지기능 사용. 상품정보 및 이미지를 수정하는 메서드
    public Long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception{
        // 상품 수정. ItemFormDto 와 이미지 파일 리스트를 파라미터로 받는 메서드. 수정된 상품의 ID를 Long 형태로 반환한다.
        Item item = itemRepository.findById(itemFormDto.getId()) // 상품등록 화면으로부터 전달받은 상품 아이디 이용해 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new); // 주어진 itemFormDto 를 사용해 상품 엔티티 조회. 만약 해당 ID로 상품이 존재하지 않으면 예외 발생
        item.updateItem(itemFormDto); // 상품 등록 화면으로부터 전달 받은 itemFormDto 를 통해 상품 엔티티 업데이트
        // 조회한 item 객체에 대해 updateItem(상품업데이트 관련 비즈니스로직) 메서드를 호출하여 itemFormDto 의 정보로 업데이트한다.

        List<Long> itemImgIds = itemFormDto.getItemImgIds(); // 상품이미지 아이디 리스트 조회
        // 이 리스트는 수정된 이미지의 ID들을 나타내며, 수정 과정에서 어떤 이미지가 수정되었는지 나타낼수 있다.

        // 이미지 등록
        for (int i = 0 ; i<itemImgFileList.size(); i++){ // 이미지 파일 리스트를 순회하며 각 이미지들을 처리하는 루프
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i)); // itemImgService의 updateItemImg 메서드를 호출하여 상품 이미지 정보를 업데이트 한다.
            // 상품 이미지업데이트를 위해 updateItemImg() 메소드에 상품 이미지 아이디와,
            // 상품 이미지 파일 정보를 파라미터로 전달한다.
            // 상품이미지 ID 와 이미지 파일을 받아서 데이터베이스 상의 이미지 정보를 업데이트 하는 역할수행
        }
        return item.getId();
        // 주어진 itemFormDto 와 이미지 파일 리스트를 사용하여 상품 정보 및 이미지를 업데이트 하는 역할을 한다.
        // 이미지 업데이트는 이미지 ID 리스트를 사용하여 각 이미지의 업데이트를 수행한다.
    }



    // 상품 조회 조건과 페이지 정보를 파라미터로 받아서 상품데이터를 조회하는 getUserItemPage() 메서드 추가
    // 데이터의 수정이 일어나지 않음으로 최적화를 위해 Transactional 사용
    @Transactional(readOnly = true)
    public Page<Item> getUserItemPage(ItemSearchDto itemSearchDto,
                                      Pageable pageable) {
        // 주어진 itemSearchDto 와 pageable 을 사용하여 사용자의 상품 목록을 페이지별로 조회하는 메서드
        // Page<Item> 을 반환한다.
        return itemRepository.getUserItemPage(itemSearchDto, pageable);
        // itemRepository 의 getUserItemPage 메서드를 호출하여 사용자의 상품 목록을 페이지별로 조회한다.

        // 주어진 검색 조건과 페이지 정보를 사용하여 사용자의 상품 목록을 페이지별로 조회하고,
        // 조회 결과를 Page<Item> 형태로 반환한다.
    }



    // 상품데이터조회
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,
                                             Pageable pageable){
        // 주어진 itemSearchDto 와 pageable 을 사용하여 메인 페이지에 표시할 상품 목록을 페이지별로 조회하는 메서드
        // Page<MainItemDto> 를 반환한다.
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
        // 메인페이지에 표시할 상품 목록을 페이지별로 조회한다.

        // 주어진 검색 조건과 페이지정보를 사용하여 메인 페이지에 표시할 상품 목록을 페이지별로 조회
        // 조회 결과를 Page<MainItemDto> 형태로 반환한다.
        // MainItemDto 는 메인 페이지에 표시할 때 필요한 상품 정보를 담는 Dto이다.
    }
}


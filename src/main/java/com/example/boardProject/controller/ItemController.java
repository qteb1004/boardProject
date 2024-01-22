package com.example.boardProject.controller;

import com.example.boardProject.dto.ItemFormDto;
import com.example.boardProject.dto.ItemSearchDto;
import com.example.boardProject.entity.Item;
import com.example.boardProject.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    ItemService itemService;

    @GetMapping("/item/new")
    public String itemForm(Model model, ItemFormDto itemFormDto) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    } // /item/new 경로로 요청이 들어왔을때 실행. 주로 웹 어플리케이션의 화면을 표시하는데 사용
    @PostMapping("/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult
                          bindingResult, Model model, @RequestParam("itemImgFile") List<MultipartFile>
                          itemImgFileList){
        // @Valid : 유효성 검사 어노테이션
        // "itemImgFile" 파라미터 이름으로 전송된 파일들을 받는데 사용한다. 이것은 상품 이미지 파일 업로드를 처리하기 위한것

        if(bindingResult.hasErrors()){ // 상품 등록 시 필수값이 없다면 다시 상품 등록 페이지로 전환한다.
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null ){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
            // 상품 등록시 첫번째 이미지가 없다면 에러 메세지와 함께 상품 등록 페이지로 전환
            // 상품의 첫 번째 이미지는 메인 페이지에서 보여줄 상품 이미지로 사용하기 위해서 필수 값으로 지정
            // 첫번째 상품이미지가 비어있고 isEmpty() itemFormDto 객체의 id 가 null 인 경우,
            // 오류메세지를 추가하고 item/itemForm 뷰로 이동한다.
            // 이것은 첫 번째 상품 이미지가 필수 입력 값이라는 조건을 나타낸다.
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
            // 상품등록을 시도한다. 이 과정에서 itemService 를 사용하여 데이터베이스에 상품 정보를 저장하고,
            // 상품 이미지 파일을 업로드한다. 에러가 발생하면 오류 메세지를 추가하고 item/itemForm 뷰로 이동한다.

        } catch (Exception e){ // 예외가 발생했을때 실행되는 블록 정의. 여기서 Exception 은 모든 예외를 처리하는 예외 클래스..
            // 이 블록은 예외가 발생한 경우 실행된다.
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm"; // 예외가 발생한경우 위 메세지를 띄우며 item/itemForm 뷰로 이동한다.
            // 상품 저장로직호출.
            // 매개변수로 상품정보와 상품 이미지정보를 담고 있는 itemImgFileList 를 넘겨준다
        }
        return "redirect:/main"; // 상품이 정상적으로 등록되었다면 메인페이지로 이동한다.

        // 예외 처리 블록을 사용하여 예외가 발생한 경우 적절한 처리를 수행하고 사용자에게 오류 메세지를
        // 표시한 뒤, 마지막으로 메인 페이지로 리다이렉트 하는 로직
    }






    // 상품수정페이지 진입
    /* @GetMapping("/item/{itemId}")
    public String itemDt1(@PathVariable("itemId") Long itemId, Model model) {

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId); // 조회한 상품 담아서 뷰로 전달
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e){ // 상품 엔티티가 존재하지 않을 경우 에러메세지 담아서 상품 등록 페이지로 이동
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    } */
    // 상품 수정 URL
    @PostMapping("/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,
                             BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile>
                             itemImgFileList, Model model){

        if (bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null ) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
            //itemImgFileList.get(0) : itemImgFileList 리스트의 첫 번째 요소(인덱스0) 에 접근한다.
            // 여기서 itemImgFileList 는 파일을 나타내는 객체의 리스트로, 각 객체는 업로드 된 이미지를 나타낸다.

            // .imEmpty() 메소드는 이전 단게에서 얻은 요소에 적용되는데, 첫번째 파일이 비어있는지 여부를 확인한다.
            // 파일 업로드의 맥락에서 비어있는 파일은 일반적으로 해당 필드에 파일이 선택되지 않았거나 업로드되지 않았음을 의미한다.


            // 이러한 조건들을 조합하면
            // itemImgFileList 의 첫 번째 파일이 비어있는지(즉, 이미지가 선택되지 않았는지) 확인하고
            // itemFormDto 객체의 id 가 null 인지 확인한다. 아이템 수정의 맥락에서
            // id 가 null 이면 아이템이 생성되는 것이 아니라 수정되는 것을 나타낼 수 있다.
        }
        try {
            itemService.updateItem(itemFormDto, itemImgFileList); // 상품수정로직 호출
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/main";
    }




    // 상품관리화면 이동 및 조회 한 상품 데이터를 화면에 전달
    // 한페이지 당 총 3개의 상품만 보여주고 페이지 번호는 0부터 시작
    @GetMapping({"/items", "/items/{page}"}) // 상품관리화면 진입 시 URL 에 페이지 번호가 없는 경우와 페이지 번호가 있는경우 2가지 매핑
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable("page") Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,3); // 페이징처리. 조회할페이지번호,한번에 가지고 올 데이터 수
        Page<Item> items =
                itemService.getUserItemPage(itemSearchDto, pageable); // 조회조건과 페이징정보를 파라미터로 전달
        model.addAttribute("items", items); // 조회한 상품 데이터 및 페이징 정보 뷰에 전달
        model.addAttribute("itemSearchDto", itemSearchDto); // 페이지 전환 시 기존 검색 조건 유지
        model.addAttribute("maxPage", 5); // 상품관리메뉴 하단에 보여줄 페이지 번호의 최대 개수
        return "item/itemMng";
    }




    //  상품 상세 페이지. 기존 상품 수정 페이지 구현에서 미리 만들어 둔 상품을 가지고 오는 로직
    @GetMapping("/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

}

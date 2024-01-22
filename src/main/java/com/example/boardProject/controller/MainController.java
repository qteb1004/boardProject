package com.example.boardProject.controller;

import com.example.boardProject.dto.ItemSearchDto;
import com.example.boardProject.dto.MainItemDto;
import com.example.boardProject.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    ItemService itemService;

    // 메인 페이지에 상품 데이터 보여주기
    @GetMapping("/main")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page,
                       Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items =
                itemService.getMainItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "main";
    }
}

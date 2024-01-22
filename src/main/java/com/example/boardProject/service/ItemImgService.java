package com.example.boardProject.service;

import com.example.boardProject.entity.ItemImg;
import com.example.boardProject.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;
    // 프로퍼티스에 등록한 itemImgLocation 값을 불러와 itemImgLocation 변수에 넣는다.

    @Autowired
    ItemImgRepository itemImgRepository;

    @Autowired
    FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile)
        throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes()); // 사용자가 상품의 이미지를 등록했다면 저장할 경로와 파일이름,파일을 파일의
            // 바이트 배열을 파일 업로드 파라미터로 uploadFile메소드 호출. 호출결과 로컬에 저장된 파일의 이름을 imgName 변수에 저장
            imgUrl = "/images/item/" + imgName;
            // 저장한 상품 이미지를 불러올 경로를 설정. 외부 리소스를 불러오는 urlPatterns로 WebMvcConfig 클래스에서
            // "/imeges/** 를 설정해주었다. 또 프로퍼티스에서 설정한 uploadPath 프로퍼티 경로인 "C:/shop/" 아래 item
            // 폴더에 이미지를 저장하므로 상품 이미지를 불러오는 경로로 "/images/item/"를 붙여준다.
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
        // imgName:실제 로컬에 저장된 상품 이미지 파일의 이름
        // oriImgName:업로드했던 상품이미지의 원래이름
        // imgUrl:업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
    }




    // 상품 이미지 수정 - 상품 이미지 데이터를 수정할 때 변경감지 기능 사용
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)
        throws Exception{
        if (!itemImgFile.isEmpty()){ // 상품 이미지를 수정한 경우 상품 이미지를 업데이트 한다.
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId) // 상품 이미지 아이디를 이용해 기존 저장했던 상품 이미지 엔티티를 조회한다.
                    .orElseThrow(EntityNotFoundException::new);
            // 기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){ // 기존에 등록된 상품 이미지 파일이 잇을 경우 해당 파일을 삭제한다.
                fileService.deleteFile(itemImgLocation + "/" +
                        savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation,
                    oriImgName, itemImgFile.getBytes()); // 업데이트한 상품 이미지 파일을 업로드한다.
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
            // 변경된 상품 이미지 정보를 세팅
            // 여기서 중요한점은 상품 등록때처럼 itemImgRepository.save() 로직을 호출하지 않는다는 것이다.
            // savedItemImg 엔티티는 현재 영속 상태이므로 데이터를 변경하는 것만으로 변경감지기능이 동작하여
            // 트랜젝션이 긑날때 update 쿼리가 실행된다. 여기서 중요한 것은 엔티티가 영속 상태여야 한다는 것이다.
        }
    }
}

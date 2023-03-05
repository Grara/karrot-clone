package com.karrotclone.api;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.dto.*;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 판매글과 관련된 API요청을 처리하는 컨트롤러입니다.
 * @since 2023-02-23
 * @createdBy 노민준
 */
@RequiredArgsConstructor
@RestController
public class SalesPostApiController {

    private final TempMemberRepository tempMemberRepository; //임시로 사용할 멤버 DAO객체
    private final SalesPostRepository salesPostRepository; //거래글 DAO
    private final AmazonS3Client amazonS3Client;
    private String bucketName = "helloshop-build";
    /**
     * 사용자가 입력한 데이터를 바탕으로 판매글을 생성합니다.
     * @param form 생성할 판매글 데이터 폼
     * @return 생성한 판매글의 ID값
     * @since 2023-02-23
     * @cretedBy 노민준
     * @lastModified 2023-03-03
     */
    @ApiOperation(value="거래글 생성 요청",
            notes="제출한 데이터를 바탕으로 거래글을 생성합니다. 성공적으로 생성됐을 경우 생성된 거래글의 ID값을 반환합니다. (ver.1)")
    @PostMapping("/api/v1/post")
    public ResponseEntity<ResponseDto> createSalesPost(CreateSalesPostForm form) throws IOException {

        ResponseDto dto = new ResponseDto();

        try {
            Member member = //임시 멤버 사용
                    tempMemberRepository.findByNickName("user").orElseThrow(() -> new NoSuchElementException("유저가 없습니다."));

            SalesPost post = SalesPost.createByForm(form, member); //거래글 생성

            //이미지 s3에 업로드 후 반환할 DTO의 이미지 url리스트에 url 추가
            for (MultipartFile image : form.getImages()) {

                String originName = image.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String extention = originName.substring(originName.lastIndexOf(".")); //확장자
                String saveName = uuid + extention;
                long size = image.getSize();

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(image.getContentType());
                objectMetadata.setContentLength(size);

                amazonS3Client.putObject(
                        new PutObjectRequest(bucketName, saveName, image.getInputStream(), objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead)
                );

                String imagePath = amazonS3Client.getUrl(bucketName, saveName).toString();

                post.getImageUrls().add(imagePath); //거래글 엔티티에 url 저장
            }

            /*
            이미지가없을 경우 url을 "없음"으로 추가
            굳이 넣는 이유는 판매자의 다른글 불러오기할 때 N+1을 막기위해 이미지 url을 페치조인하는데
            이미지url이 아무것도 없으면 거래글 조회가 안됨
            */
            if (post.getImageUrls().isEmpty()) {
                post.getImageUrls().add("없음");
            }

            Long id = salesPostRepository.save(post).getId(); //거래글 저장 후 생성된 ID값

            dto.setData(id);
            dto.setMessage("거래글이 성공적으로 생성되었습니다. data는 거래글의 id입니다.");
            return new ResponseEntity<>(dto, HttpStatus.CREATED);

        }catch (IllegalArgumentException e){
            dto.setData(e.getMessage());
            dto.setMessage("거래글의 생성에 실패했습니다. data는 에러메시지 입니다.");
            return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * id에 해당하는 거래글의 상세페이지 정보를 가져옵니다.
     * @param id 표시할 거래글의 id값
     * @return 거래글 상세글 표시에 필요한 데이터 DTO
     * @since 2023-02-24
     * @createdBy 노민준
     * @lastModified 2023-03-03
     */
    @ApiOperation(value="거래글 상세페이지 정보 가져오기", notes="id에 해당하는 거래글의 상세페이지 정보를 가져옵니다. (ver.1)")
    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<ResponseDto> getSalesPostDetail(@PathVariable("id") Long id){

        ResponseDto responseDto = new ResponseDto();

        try {
            //id로 거래글을 찾기
            SalesPost findPost =
                    salesPostRepository.findById(id).orElseThrow(() -> new NoSuchElementException("id에 해당하는 거래글이 없습니다."));

            //DTO로 변환
            SalesPostDetailDto detailDto = new SalesPostDetailDto(findPost);

            //현재 거래글을 올린 판매자의 다른 최신 거래글 DTO 리스트, 최대 4개
            List<SalesPostSimpleDto> postsFromSeller =
                    salesPostRepository.findTop4ListBySeller(findPost.getMember(), id)
                            .stream().map(SalesPostSimpleDto::new).collect(Collectors.toList());

            //상세페이지 DTO안에 거래글 리스트 추가
            detailDto.setPostsFromSeller(postsFromSeller);

            responseDto.setMessage("id에 해당하는 거래글의 정보를 정상적으로 가져왔습니다.");
            responseDto.setData(detailDto);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        }catch (NoSuchElementException e){ //거래글을 찾지 못했을 경우
            responseDto.setMessage(e.getMessage());
            responseDto.setData(null);

            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 홈 화면에서 거래글 목록 DTO를 가져옵니다.
     * @param condition 검색 조건
     * @param pageable 페이징에 필요한 파라미터값, page=n 형식으로 보내면 됨
     * @return
     * @since 2023-03-03
     * @createdBy 노민준
     */
    @ApiOperation(value="거래글 목록 가져오기", notes="페이징 정보와 검색조건을 바탕으로 거래글 DTO 리스트를 가져옵니다.")
    @GetMapping("/api/v1/post")
    public ResponseEntity<ResponseDto> getPosts(@ModelAttribute SalesPostSearchCondition condition, Pageable pageable){

        ResponseDto dto = new ResponseDto();

        try {
            Member member = //임시 멤버 사용
                    tempMemberRepository.findByNickName("user").orElseThrow(() -> new NoSuchElementException("유저가 없습니다."));

            dto.setMessage("거래 목록을 가져오는데 성공했습니다.");
            dto.setData(salesPostRepository.findListWithSlice(member, condition, pageable));
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch (Exception e){
            dto.setMessage("처리하는 도중 오류가 발생했습니다.");
            dto.setData(e);
            return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.karrotclone.api;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.dto.CreateSalesPostForm;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.dto.SalesPostDetailDto;
import com.karrotclone.dto.SalesPostSimpleDto;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    /**
     * 사용자가 입력한 데이터를 바탕으로 판매글을 생성합니다.
     * @param form 생성할 판매글 데이터 폼
     * @return 생성한 판매글의 ID값
     * @since 2023-02-23
     * @cretedBy 노민준
     * @lastModified 2023-02-23
     */
    @ApiOperation(value="거래글 생성 요청",
            notes="제출한 데이터를 바탕으로 거래글을 생성합니다. 성공적으로 생성됐을 경우 생성된 거래글의 ID값을 반환합니다. 현재는 임시구현 상태")
    @PostMapping("/api/post")
    public ResponseEntity<ResponseDto> createSalesPost(@RequestBody CreateSalesPostForm form){

        Member member = //임시 멤버 사용
                tempMemberRepository.findByNickName("user").orElseThrow(() -> new NoSuchElementException("유저가 없습니다."));

        SalesPost post = new SalesPost(form, member); //거래글 생성
        Long id = salesPostRepository.save(post).getId(); //거래글 저장 후 생성된 ID값

        ResponseDto dto = new ResponseDto();
        dto.setData(id);
        dto.setMessage("거래글이 성공적으로 생성되었습니다. data는 거래글의 id입니다.");

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * @param id 표시할 거래글의 id값
     * @return 거래글 상세글 표시에 필요한 데이터 DTO
     * @since 2023-02-24
     * @createdBy 노민준
     * @lastModified 2023-02-24
     */
    @ApiOperation(value="거래글 상세페이지 정보 가져오기", notes="id에 해당하는 거래글의 상세페이지 정보를 가져옵니다.")
    @GetMapping("/api/post/{id}")
    public ResponseEntity<ResponseDto> getSalesPostDetail(@RequestParam("id") Long id){

        ResponseDto responseDto = new ResponseDto();

        try {
            //id로 거래글을 찾기
            SalesPost findPost =
                    salesPostRepository.findById(id).orElseThrow(() -> new NoSuchElementException("id에 해당하는 거래글이 없습니다."));

            //DTO로 변환
            SalesPostDetailDto detailDto = new SalesPostDetailDto(findPost);

            //현재 거래글을 올린 판매자의 다른 최신 거래글 DTO 리스트, 최대 4개
            List<SalesPostSimpleDto> postsFromSeller =
                    salesPostRepository.findDistinctTop4ByMemberAndIdNotOrderByIdDesc(findPost.getMember(), id)
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

}

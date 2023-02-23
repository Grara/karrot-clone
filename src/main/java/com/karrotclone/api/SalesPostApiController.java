package com.karrotclone.api;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.dto.CreateSalesPostForm;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.Optional;

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
                tempMemberRepository.findByNickName("user").orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));

        SalesPost post = new SalesPost(form, member); //거래글 생성
        Long id = salesPostRepository.save(post).getId(); //거래글 저장 후 생성된 ID값

        ResponseDto dto = new ResponseDto();
        dto.setData(id);
        dto.setMessage("거래글이 성공적으로 생성되었습니다. data는 거래글의 id입니다.");

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}

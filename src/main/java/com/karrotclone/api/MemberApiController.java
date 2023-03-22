package com.karrotclone.api;

import com.karrotclone.domain.Favorite;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.dto.RegisterDto;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.dto.SalesPostDetailDto;
import com.karrotclone.dto.SalesPostSimpleDto;
import com.karrotclone.exception.DomainNotFoundException;
import com.karrotclone.repository.FavoriteRepository;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import com.karrotclone.service.auth.RegisterService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 회원과 관련된 API요청을 처리해주는 컨트롤러입니다.
 *
 * @createdBy 노민준
 * @since 2023-02-22
 */

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final SalesPostRepository salesPostRepository;
    private final TempMemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;
    private final RegisterService registerService;

    /**
     * 전달받은 폼 데이터로 회원가입을 진행합니다.
     *
     * @param dto           회원가입 요청 시 필요한 데이터 DTO
     * @param bindingResult dto의 값이 제약을 어길 경우 오류를 담는 객체
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value = "회원가입 요청", notes = "입력된 정보를 토대로 회원가입을 진행합니다.")
    @PostMapping("/api/v1/members")
    public ResponseEntity<ResponseDto> register(final @Valid RegisterDto dto, @ApiIgnore BindingResult bindingResult) throws IOException {

        ResponseDto resDto = new ResponseDto();

        if (bindingResult.hasErrors()) {
            System.out.println(" Incoming error ");
            resDto.setMessage("입력한 정보가 문제가 있습니다. data는 오류정보입니다.");
            resDto.setData(bindingResult.getAllErrors());
            return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
        }

        registerService.register(dto);
        System.out.println("회원 가입이 완료되었습니다.");
        resDto.setMessage("회원가입에 성공했습니다.");
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);

    } //추후에 다른 로그인 Auth 구현 가능.

}

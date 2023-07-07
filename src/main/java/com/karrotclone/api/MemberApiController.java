package com.karrotclone.api;

import com.karrotclone.domain.Member;
import com.karrotclone.dto.RegisterDto;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.repository.FavoriteRepository;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.MemberRepository;
import com.karrotclone.service.auth.RegisterService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;

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
    private final MemberRepository memberRepository;
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
            resDto.setMessage("입력한 정보가 문제가 있습니다. data는 오류정보입니다.");
            resDto.setData(bindingResult.getAllErrors());
            return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
        }

        registerService.register(dto);
        resDto.setMessage("회원가입에 성공했습니다.");
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);

    } //추후에 다른 로그인 Auth 구현 가능.

    @ApiOperation(value = "FCM토큰 등록", notes = "유저의 FCM토큰을 등록합니다.")
    @PostMapping("/api/v1/members/register-fcmtoken")
    public ResponseEntity<ResponseDto> registerFcmToken(@RequestBody String token, @ApiIgnore @AuthenticationPrincipal Member member){
        member.setFcmToken(token);
        memberRepository.save(member);
        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("FCM토큰이 정상적으로 등록되었습니다.");
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}

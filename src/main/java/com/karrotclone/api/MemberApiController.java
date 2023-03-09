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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @ApiOperation(value="회원가입 요청", notes="입력된 정보를 토대로 회원가입을 진행합니다.")
    @PostMapping("/api/v1/members")
    public void register(final @Valid @RequestBody RegisterDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            System.out.println(" Incoming error ");
            return;
        }
        try {
            registerService.register(dto);
            System.out.println("회원 가입이 완료되었습니다.");
        }catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("register failed","이미 등록된 메일입니다.");
        } catch (Exception e){
            System.out.println("error = " + e);
            e.printStackTrace();
            bindingResult.reject("register failed", e.getMessage());
        }
    } //추후에 다른 로그인 Auth 구현 가능.


    /**
     * 거래글을 관심목록에 추가하거나, 이미 관심목록에 있다면 삭제합니다.
     * @param postId 거래글의 id
     * @return 생성된 Favorite id
     * @lastModified 2023-03-07 노민준
     */
    @ApiOperation(value="관심목록 추가 or 삭제 요청", notes = "거래글을 관심목록에 추가하거나, 이미 관심목록에 있다면 삭제합니다.")
    @PostMapping("/api/v1/favorites")
    public ResponseEntity<ResponseDto> switchFavorite(@RequestBody Long postId) {

        ResponseDto resDto = new ResponseDto();

        Member member = memberRepository.findByNickName("user").get();

        SalesPost post =
                salesPostRepository.findById(postId).orElseThrow(() -> new DomainNotFoundException("id에 해당하는 거래글이 없습니다."));

        List<Favorite> existingList = favoriteRepository.findListByPostAndMember(post, member);

        if (existingList.isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setMember(member);
            favorite.setPost(post);

            post.addFavoriteCount(); //거래글의 관심 수 추가

            salesPostRepository.save(post);
            Long id = favoriteRepository.save(favorite).getId();

            resDto.setMessage("성공적으로 관심목록에 추가됐습니다.");
            resDto.setData(null);

            return new ResponseEntity<>(resDto, HttpStatus.OK);

        } else { //현재 회원이 같은 거래글을 이미 관심목록에 추가했다면
            Favorite favorite = existingList.get(0);
            favoriteRepository.delete(favorite);

            resDto.setMessage("성공적으로 관심목록에서 제거되었습니다.");
            resDto.setData(null);

            return new ResponseEntity<>(resDto, HttpStatus.OK);
        }
    }

    /**
     * 회원의 관심목록을 가져옵니다.
     * @return 관심목록에 있는 거래글들의 DTO 리스트
     * @since 2023-03-05
     */
    @ApiOperation(value="관심목록 가져오기 요청", notes = "회원의 관심목록을 가져옵니다.")
    @GetMapping("/api/v1/favorites")
    public ResponseEntity<ResponseDto> getFavorites(Pageable pageable) {

        ResponseDto resDto = new ResponseDto();

        Member member = memberRepository.findByNickName("user").get();

        List<Favorite> favList = favoriteRepository.findListByMember(member);

        //거래글을 DTO로 변환
        List<SalesPostSimpleDto> postDtoList =
                favList.stream()
                        .map(fav -> new SalesPostSimpleDto(fav.getPost()))
                        .collect(Collectors.toList());

        resDto.setMessage("성공적으로 관심목록을 가져왔습니다");
        resDto.setData(postDtoList);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}

package com.karrotclone.api;

import com.karrotclone.domain.Favorite;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.dto.SalesPostDetailDto;
import com.karrotclone.dto.SalesPostSimpleDto;
import com.karrotclone.repository.FavoriteRepository;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    /**
     * id에 해당하는 거래글을 관심목록에 추가합니다.
     * @param postId 추가할 거래글의 id
     * @return 생성된 Favorite id
     */
    @PostMapping("/api/v1/favorites")
    public ResponseEntity<ResponseDto> addFavorite(@RequestBody Long postId) {

        ResponseDto dto = new ResponseDto();

        Member member = memberRepository.findByNickName("user").get();

        SalesPost post =
                salesPostRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("id에 해당하는 거래글이 없습니다."));

        List<Favorite> existingList = favoriteRepository.findListByPostAndMember(post, member);

        if (existingList.isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setMember(member);
            favorite.setPost(post);
            Long id = favoriteRepository.save(favorite).getId();

            dto.setMessage("성공적으로 관심목록에 추가됐습니다.");
            dto.setData(id);

            return new ResponseEntity<>(dto, HttpStatus.CREATED);

        } else {
            dto.setMessage("이미 관심목록에 추가된 거래글입니다.");
            dto.setData(null);

            return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/v1/favorites")
    public ResponseEntity<ResponseDto> getFavorites() {

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

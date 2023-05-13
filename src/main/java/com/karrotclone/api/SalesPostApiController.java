package com.karrotclone.api;

import com.karrotclone.domain.Favorite;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.SalesState;
import com.karrotclone.dto.*;
import com.karrotclone.exception.DomainNotFoundException;
import com.karrotclone.repository.FavoriteRepository;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.MemberRepository;
import com.karrotclone.utils.AwsUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 판매글과 관련된 API요청을 처리하는 컨트롤러입니다.
 *
 * @createdBy 노민준
 * @since 2023-02-23
 */
@RequiredArgsConstructor
@RestController
public class SalesPostApiController {

    private final MemberRepository memberRepository; //임시로 사용할 멤버 DAO객체
    private final SalesPostRepository salesPostRepository; //거래글 DAO
    private final AwsUtil awsUtil;
    private final FavoriteRepository favoriteRepository;

    /**
     * 사용자가 입력한 데이터를 바탕으로 판매글을 생성합니다.
     *
     * @param form 생성할 판매글 데이터 폼
     * @return 생성한 판매글의 ID값
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value = "거래글 생성 요청",
            notes = "제출한 데이터를 바탕으로 거래글을 생성합니다. 성공적으로 생성됐을 경우 생성된 거래글의 ID값을 반환합니다. swagger에서 제대로 동작 안함, 다른 API툴로 시도하길 바람")
    @PostMapping("/api/v1/post")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> createSalesPost(@Valid SalesPostDataForm form,
                                                       @ApiIgnore BindingResult bindingResult,
                                                       @ApiIgnore @AuthenticationPrincipal Member member) throws IOException {

        ResponseDto resDto = new ResponseDto();

        if(bindingResult.hasErrors()){
            resDto.setMessage("제출한 데이터에 문제가 있습니다. data는 오류정보입니다.");
            resDto.setData(bindingResult.getAllErrors());
            return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
        }

        try {

            SalesPost post = SalesPost.createByFormAndMember(form, member); //거래글 생성

            //이미지 s3에 업로드 후 반환할 DTO의 이미지 url리스트에 url 추가
            for (MultipartFile image : form.getImages()) {
                String url = awsUtil.uploadToS3(image);
                post.getImageUrls().add(url);
            }

            Long id = salesPostRepository.save(post).getId(); //거래글 저장 후 생성된 ID값

            resDto.setMessage("거래글이 성공적으로 생성되었습니다. data는 거래글의 id입니다.");
            resDto.setData(id);
            return new ResponseEntity<>(resDto, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            resDto.setMessage("거래글의 생성에 실패했습니다. 오류메시지 : " + e.getMessage());
            return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 거래글의 상세정보를 가져옵니다.
     * @param id 거래글의 id값
     * @return 거래글 상세정보 DTO
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value = "거래글 상세페이지 정보 가져오기", notes = "id에 해당하는 거래글의 상세페이지 정보를 가져옵니다.")
    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<ResponseDto> getDetails(@PathVariable("id") Long id) {

        //id로 거래글을 찾기
        SalesPost findPost =
                salesPostRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("id에 해당하는 거래글이 없습니다."));

        //DTO로 변환
        SalesPostDetailDto detailDto = new SalesPostDetailDto(findPost);

        //현재 거래글을 올린 판매자의 다른 최신 거래글 DTO 리스트, 최대 4개
        List<SalesPostSimpleDto> postsFromSeller =
                salesPostRepository.findTop4ListBySeller(findPost.getMember(), id)
                        .stream().map(SalesPostSimpleDto::new).collect(Collectors.toList());

        //상세페이지 DTO안에 거래글 리스트 추가
        detailDto.setPostsFromSeller(postsFromSeller);

        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("id에 해당하는 거래글의 정보를 정상적으로 가져왔습니다.");
        resDto.setData(detailDto);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 홈 화면에서 거래글 목록을 가져옵니다.
     * @param condition 검색 조건
     * @param pageable  페이징에 필요한 파라미터값, page=n 형식으로 보내면 됨
     * @return 거래글 DTO 리스트를 지닌 Slice 객체
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value = "홈화면 거래글 목록 가져오기", notes = "홈화면에 필요한 거래글 DTO 리스트를 가져옵니다. 페이징은 swagger에서 제대로 테스트 못함, 노션 참조 바람")
    @GetMapping("/api/v1/post/home-list")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> getHomeList(@ModelAttribute PostHomeSearchCondition condition,
                                                   @ApiIgnore Pageable pageable,
                                                   @ApiIgnore @AuthenticationPrincipal Member member) {

        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("거래 목록을 가져오는데 성공했습니다.");
        resDto.setData(salesPostRepository.findHomeList(member, condition, pageable));
        return new ResponseEntity<>(resDto, HttpStatus.OK);

    }

    /**
     * 판매자가 판매하는 다른 거래글을 가져옵니다.
     * @param condition 검색조건
     * @param pageable 페이징에 필요한 파라미터 정보
     * @return 거래글 DTO 리스트를 지닌 Slice 객체
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value = "판매자의 다른 거래글 가져오기", notes = "판매자의 다른 거래글을 가져옵니다. 페이징은 swagger에서 제대로 테스트 못함, 노션 참조 바람")
    @GetMapping("/api/v1/post/seller-list")
    public ResponseEntity<ResponseDto> getSellerList(@ModelAttribute PostSellerSearchCondition condition,
                                                     @ApiIgnore Pageable pageable){

        ResponseDto resDto = new ResponseDto();

        if(!StringUtils.hasText(condition.getEmail())) {
            resDto.setMessage("판매자이메일은 필수입니다.");
            return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
        }

        Slice<SalesPostSimpleDto> result = salesPostRepository.findAllListBySeller(condition, pageable);

        resDto.setMessage("해당 판매자의 판매글 목록을 성공적으로 가져왔습니다.");
        resDto.setData(result);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 나의 판매글 목록을 가져옵니다.
     * @param condition 검색조건
     * @param pageable 페이징 파라미터
     * @return 거래글 DTO 리스트를 지닌 Slice 객체
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value="나의 판매글 목록 가져오기", notes = "나의 판매글 목록을 가져옵니다. 페이징은 swagger에서 제대로 테스트 못함, 노션 참조 바람")
    @GetMapping("/api/v1/post/my-sales-list")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> getMySalesList(@ModelAttribute MySalesSearchCondition condition,
                                                      @ApiIgnore Pageable pageable,
                                                      @ApiIgnore @AuthenticationPrincipal Member member){


        if(condition.getSalesState() == null) { //파라미터 입력이 없다면 거래상태는 판매중으로
            condition.setSalesState(SalesState.DEFAULT);
        }

        if(condition.getIsHide() == null) //숨김여부가 null이면 기본적으로 false 설정
            condition.setIsHide(false);

        if(condition.getIsHide()){ //숨긴글을 찾을 때는 거래상태는 상관없어야 함
            condition.setSalesState(null);
        }

        Slice<SalesPostSimpleDto> result = salesPostRepository.findMySalesList(member, condition, pageable);

        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("성공적으로 나의 판매글 목록을 가져왔습니다.");
        resDto.setData(result);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 거래글의 정보를 수정합니다.
     * @param id 변경할 거래글의 id
     * @param form 적용할 폼 데이터
     * @return 거래글의 id
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value="거래글 수정", notes="거래글의 정보를 수정합니다. swagger에서 제대로 동작 안함, 다른 API툴로 시도하길 바람")
    @PatchMapping("/api/v1/post/{id}")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> updatePost(@Valid SalesPostDataForm form,
                                                  @ApiIgnore BindingResult bindingResult,
                                                  @PathVariable("id")Long id,
                                                  @ApiIgnore @AuthenticationPrincipal Member member) throws IOException{
        ResponseDto resDto = new ResponseDto();

        if(bindingResult.hasErrors()){
            resDto.setMessage("제출한 데이터에 문제가 있습니다. data는 오류정보입니다.");
            resDto.setData(bindingResult.getAllErrors());
            return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
        }

        SalesPost post =
                salesPostRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("id에 해당하는 거래글이 없습니다"));

        if(!post.getMember().equals(member)){
            resDto.setMessage("수정하려는 거래글의 판매자와 현재 로그인한 회원이 다릅니다.");
            return new ResponseEntity<>(resDto, HttpStatus.FORBIDDEN);
        }

        post.updateByForm(form);

        //거래글의 기존 이미지 전부 삭제
        for(String url : post.getImageUrls()){
            awsUtil.deleteAtS3(url);
        }

        //이미지 리스트 초기화
        post.setImageUrls(new ArrayList<>());

        //폼에 있는 이미지 업로드 후 거래글에 url 추가
        for(MultipartFile image : form.getImages()){
            String url = awsUtil.uploadToS3(image);
            post.getImageUrls().add(url);
        }

        salesPostRepository.save(post);


        resDto.setMessage("거래글 수정에 성공했습니다. data는 거래글의 id입니다.");
        resDto.setData(id);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 거래글의 판매 상태를 변경합니다.
     * @param id 변경할 거래글 id
     * @param state 적용시킬 상태
     * @return 성공 시 true 반환
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value = "거래글의 판매 상태 변경", notes="거래글의 판매 상태를 변경합니다.")
    @PostMapping("/api/v1/post/{id}/change-state")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> changeState(@PathVariable("id")Long id,
                                                   @RequestBody SalesState state,
                                                   @ApiIgnore @AuthenticationPrincipal Member member){
        ResponseDto resDto = new ResponseDto();

        SalesPost post =
                salesPostRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("id에 해당하는 거래글이 없습니다"));

        if(!post.getMember().equals(member)){
            resDto.setMessage("수정하려는 거래글의 판매자와 현재 로그인한 회원이 다릅니다.");
            return new ResponseEntity<>(resDto, HttpStatus.FORBIDDEN);
        }

        post.setSalesState(state);

        salesPostRepository.save(post);


        resDto.setMessage("거래글의 판매 상태 변경에 성공했습니다. data는 현재 적용된 판매상태입니다.");
        resDto.setData(post.getSalesState());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 거래글의 숨기기 여부를 변경합니다.
     * @param id 변경할 거래글 id
     * @return 현재 적용된 숨기기 여부
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value="거래글의 숨기기 여부 변경", notes="거래글의 숨기기 여부를 변경합니다.")
    @PostMapping("/api/v1/post/{id}/change-hide")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> changeHide(@PathVariable("id")Long id,
                                                  @ApiIgnore @AuthenticationPrincipal Member member){

        ResponseDto resDto = new ResponseDto();
        SalesPost post =
                salesPostRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("id에 해당하는 거래글이 없습니다"));

        if(!post.getMember().equals(member)){
            resDto.setMessage("수정하려는 거래글의 판매자와 현재 로그인한 회원이 다릅니다.");
            return new ResponseEntity<>(resDto, HttpStatus.FORBIDDEN);
        }

        post.setHide(!post.isHide()); //현재 숨기기 여부의 반대 상태로 바꿔줌

        SalesPost save = salesPostRepository.save(post);


        resDto.setMessage("거래글의 숨기기여부 변경에 성공했습니다. data는 현재 적용된 숨기기여부입니다.");
        resDto.setData(post.isHide());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 거래글을 삭제합니다.
     * @param id 삭제할 거래글의 id
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value = "거래글 삭제", notes="거래글을 삭제합니다.")
    @DeleteMapping("/api/v1/post/{id}")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id,
                                              @ApiIgnore @AuthenticationPrincipal Member member){
        ResponseDto resDto = new ResponseDto();

        SalesPost post =
                salesPostRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("id에 해당하는 거래글이 없습니다"));
        
        if(!post.getMember().equals(member)){
            resDto.setMessage("삭제하려는 거래글의 판매자와 현재 로그인한 회원이 다릅니다.");
            return new ResponseEntity<>(resDto, HttpStatus.FORBIDDEN);
        }

        for(String url : post.getImageUrls()){
            awsUtil.deleteAtS3(url);
        }

        salesPostRepository.delete(post);

        resDto.setMessage("거래글 삭제에 성공했습니다.");
        resDto.setData(true);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 거래글을 관심목록에 추가하거나, 이미 관심목록에 있다면 삭제합니다.
     * @param postId 거래글의 id
     * @return 생성된 Favorite id
     * @lastModified 2023-03-21 노민준
     */
    @ApiOperation(value="관심목록 추가 or 삭제 요청", notes = "거래글을 관심목록에 추가하거나, 이미 관심목록에 있다면 삭제합니다.")
    @PostMapping("/api/v1/favorites")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> switchFavorite(@RequestBody Long postId, @ApiIgnore @AuthenticationPrincipal Member member) {

        ResponseDto resDto = new ResponseDto();

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
     * @lastModified 2023-04-27 노민준
     */
    @ApiOperation(value="관심목록 가져오기 요청", notes = "회원의 관심목록을 가져옵니다.")
    @GetMapping("/api/v1/favorites")
    @RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> getFavorites(@ApiIgnore Pageable pageable, @ApiIgnore @AuthenticationPrincipal Member member) {

        ResponseDto resDto = new ResponseDto();

        List<Favorite> favList = favoriteRepository.findListByMember(member);

        //거래글을 DTO로 변환
        List<SalesPostSimpleDto> postDtoList =
                favList.stream()
                        .map(fav -> new SalesPostSimpleDto(fav.getPost()))
                        .collect(Collectors.toList());

        if(postDtoList.isEmpty()){
            resDto.setMessage("관심목록이 비어있습니다.");
            resDto.setData(null);
            return new ResponseEntity<>(resDto, HttpStatus.OK);
        }

        resDto.setMessage("성공적으로 관심목록을 가져왔습니다");
        resDto.setData(postDtoList);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}

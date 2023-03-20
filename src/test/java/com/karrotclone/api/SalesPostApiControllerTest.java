package com.karrotclone.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karrotclone.domain.Coordinate;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.RangeStep;
import com.karrotclone.domain.enums.SalesState;
import com.karrotclone.dto.SalesPostDataForm;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@WithUserDetails("ddd") //TempInitRunner에서 저장하는 기본 유저의 이메일
class SalesPostApiControllerTest {

    @Autowired
    private SalesPostRepository salesPostRepository;

    @Autowired
    private TempMemberRepository memberRepository;

    private Member user;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper(); //객체<->JSON으로 변환해주는 매퍼

    @BeforeEach
    void setUp() {
        if(user == null)
            user = memberRepository.findByNickName("user").get(); //임시 멤버
    }

    @Test
    void 홈목록_카테고리적용_테스트() throws Exception {
        //조건에 카테고리를 적용했을 때 제대로 되는지 확인

        SalesPostDataForm form = SalesPostDataForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.VERY_CLOSE)
                .build();

        for (int i = 0; i < 3; i++) { //BOOK 3개
            SalesPost post = SalesPost.createByFormAndMember(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        form.setCategory(Category.BEAUTY); //카테고리 변경

        for (int i = 0; i < 3; i++) { //BEAUTY 3개
            SalesPost post = SalesPost.createByFormAndMember(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post/home-list?page=0&category=BOOK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(3)) //총 3개의 거래글이 조회되야함
                .andDo(print());
    }

    @Test
    void 홈목록_거리적용_테스트1() throws Exception {
        //멤버와 거래글 서로 범위에 닿는 상황 테스트

        SalesPostDataForm form = SalesPostDataForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.VERY_CLOSE)
                .build();

        /*
        유저의 현재 탐색범위는 위도와 경도 모두 0±20000 까지임
        그 이상으로 차이가 나면 탐색을 하지 못함
        즉, 2개의 거래글만 탐색되야 함
        */
        for (int i = 1; i <= 6; i++) {
            Coordinate coor = new Coordinate(10000L * i, 10000L * i, "신림", "신림");
            form.setPreferPlace(coor);
            SalesPost post = SalesPost.createByFormAndMember(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post/home-list?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2)) //총 2개의 거래글이 조회되야함
                .andDo(print());

    }

    @Test
    void 홈목록_거리적용_테스트2() throws Exception {
        //멤버만 거래글의 범위에 닿는 상황 테스트

        SalesPostDataForm form = SalesPostDataForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.FAR) //최대 탐색 범위
                .build();

        /*
        유저의 현재 탐색범위는 위도와 경도 모두 0±20000 까지임
        그 이상으로 차이가 나면 탐색을 하지 못함
        50000까지의 거래글의 탐지범위는 유저에게 닿는 상황
        하지만 유저의 탐지범위는 20000까지임
        즉, 2개의 거래글만 조회되야 함
        */
        for (int i = 1; i <= 6; i++) {
            Coordinate coor = new Coordinate(10000L * i, 10000L * i, "신림", "신림");
            form.setPreferPlace(coor);
            SalesPost post = SalesPost.createByFormAndMember(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post/home-list?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2)) //총 2개의 거래글이 조회되야함
                .andDo(print());
    }

    @Test
    void 홈목록_거리적용_테스트3() throws Exception {
        //거래글만 멤버의 범위에 닿는 상황 테스트
        Member mem = memberRepository.findByNickName("user").get();
        mem.setSearchRange(50000);
        memberRepository.save(mem);

        SalesPostDataForm form = SalesPostDataForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.VERY_CLOSE) //최소 탐색 범위
                .build();

        /*
        유저의 현재 탐색범위는 위도와 경도 모두 0±50000 까지임
        유저의 탐지범위는 50000까지 닿는 상황
        하지만 거래글들의 탐지범위는 ±20000까지임
        즉, 2개의 거래글만 조회되야 함
        */
        for (int i = 1; i <= 6; i++) {
            Coordinate coor = new Coordinate(10000L * i, 10000L * i, "신림", "신림");
            form.setPreferPlace(coor);
            SalesPost post = SalesPost.createByFormAndMember(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post/home-list?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2)) //총 2개의 거래글이 조회되야함
                .andDo(print());

    }

    @Test
    void 홈화면_숨기기_적용_테스트()throws Exception {
        SalesPostDataForm form = createTestForm();

        SalesPost post = SalesPost.createByFormAndMember(form, user);
        String id = salesPostRepository.save(post).getId().toString();

        createAndSavePost(form, user, 2);

        mockMvc.perform(post("/api/v1/post/" + id + "/change-hide"))
                .andExpect(status().isOk())
                .andDo(print());

        //총 3개의 글을 저장했고 1개를 숨겼으니 2개가 조회되야함
        mockMvc.perform(get("/api/v1/post/home-list?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2))
                .andDo(print());
    }

    @Test
    void 판매자_판매글목록_판매자이름_null_테스트()throws Exception{
        mockMvc.perform(get("/api/v1/post/seller-list"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 거래글_삭제()throws Exception{
        SalesPostDataForm form = createTestForm();

        SalesPost post = SalesPost.createByFormAndMember(form, user);
        Long id = salesPostRepository.save(post).getId();

        createAndSavePost(form, user, 3);

        mockMvc.perform(delete("/api/v1/post/" + id)) //삭제 요청
                .andExpect(status().isOk())
                .andDo(print());

        //4개 중 1개 삭제했으니 3개 조회되야함
        mockMvc.perform(get("/api/v1/post/home-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(3))
                .andDo(print());
    }

    @Test
    void 관심변경()throws Exception{

        SalesPostDataForm form = createTestForm();
        
        //1번 거래글
        SalesPost post1 = SalesPost.createByFormAndMember(form, user);
        Long id1 = salesPostRepository.save(post1).getId();
        
        //2번 거래글
        SalesPost post2 = SalesPost.createByFormAndMember(form, user);
        Long id2 = salesPostRepository.save(post2).getId();

        createAndSavePost(form, user, 3); //쩌리 3개 추가

        //1번 관심추가
        mockMvc.perform(post("/api/v1/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(id1)))
                .andExpect(status().isOk())
                .andDo(print());

        //2번 관심추가
        mockMvc.perform(post("/api/v1/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(id2)))
                .andExpect(status().isOk())
                .andDo(print());

        //2번 관심 해제
        mockMvc.perform(post("/api/v1/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(id2)))
                .andExpect(status().isOk())
                .andDo(print());

        //1개만 관심목록에 떠야함
        mockMvc.perform(get("/api/v1/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andDo(print());

    }

    @Test
    void 판매자_판매_목록_가져오기()throws Exception{

        //판매중 3개, 예약중 3개, 거래완료 3개 만든 후
        //각 상태마다 1개씩 숨기기 설정

        SalesPostDataForm form = createTestForm();

        createAndSavePost(form, user, 2);

        SalesPost post = SalesPost.createByFormAndMember(form, user);
        Long id1 = salesPostRepository.save(post).getId();

        mockMvc.perform(post("/api/v1/post/"+id1+"/change-hide"))
                .andExpect(status().isOk())
                .andDo(print());

        //예약중 3개 만들고 1개는 숨기기
        for(int i = 0; i < 3; i++){
            SalesPost postt = SalesPost.createByFormAndMember(form, user);
            Long id = salesPostRepository.save(postt).getId();

            mockMvc.perform(post("/api/v1/post/"+id+"/change-state")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(SalesState.RESERVE)))
                    .andExpect(status().isOk())
                    .andDo(print());

            if(i == 2){
                mockMvc.perform(post("/api/v1/post/"+id+"/change-hide"))
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }

        //거래완료 3개 만들고 1개 숨기기
        for(int i = 0; i < 3; i++){
            SalesPost postt = SalesPost.createByFormAndMember(form, user);
            Long id = salesPostRepository.save(postt).getId();

            mockMvc.perform(post("/api/v1/post/"+id+"/change-state")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(SalesState.COMPLETE)))
                    .andExpect(status().isOk())
                    .andDo(print());

            if(i == 2){
                mockMvc.perform(post("/api/v1/post/"+id+"/change-hide"))
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }

        //판매자의 전체글 가져오기
        //숨긴 글을 제외하고 6개를 가져와야 함
        mockMvc.perform(get("/api/v1/post/seller-list?email=ddd"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.numberOfElements").value(6))
                        .andDo(print());

        //판매자의 전체글 가져오기
        //숨기기를 제외한 판매중, 예약중 합쳐서 총 4개를 가져와야 함
        mockMvc.perform(get("/api/v1/post/seller-list?email=ddd&salesState=DEFAULT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(4))
                .andDo(print());

        //판매자의 전체글 가져오기
        //숨기기를 제외한 거래완료 2개를 가져와야 함
        mockMvc.perform(get("/api/v1/post/seller-list?email=ddd&salesState=COMPLETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2))
                .andDo(print());

    }

    @Test
    void 내_판매_목록_가져오기()throws Exception{

        //판매중 3개, 예약중 3개, 거래완료 3개 만든 후
        //각 상태마다 1개씩 숨기기 설정

        SalesPostDataForm form = createTestForm();

        createAndSavePost(form, user, 2);

        SalesPost post = SalesPost.createByFormAndMember(form, user);
        Long id1 = salesPostRepository.save(post).getId();

        mockMvc.perform(post("/api/v1/post/"+id1+"/change-hide"))
                .andExpect(status().isOk())
                .andDo(print());

        //예약중 3개 만들고 1개는 숨기기
        for(int i = 0; i < 3; i++){
            SalesPost postt = SalesPost.createByFormAndMember(form, user);
            Long id = salesPostRepository.save(postt).getId();

            mockMvc.perform(post("/api/v1/post/"+id+"/change-state")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(SalesState.RESERVE)))
                    .andExpect(status().isOk())
                    .andDo(print());

            if(i == 2){
                mockMvc.perform(post("/api/v1/post/"+id+"/change-hide"))
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }

        //거래완료 3개 만들고 1개 숨기기
        for(int i = 0; i < 3; i++){
            SalesPost postt = SalesPost.createByFormAndMember(form, user);
            Long id = salesPostRepository.save(postt).getId();

            mockMvc.perform(post("/api/v1/post/"+id+"/change-state")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(SalesState.COMPLETE)))
                    .andExpect(status().isOk())
                    .andDo(print());

            if(i == 2){
                mockMvc.perform(post("/api/v1/post/"+id+"/change-hide"))
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }

        //내 판매중인 글 가져오기
        //숨긴 글을 제외하고 판매중, 예약중 합쳐서 4개를 가져와야 함
        mockMvc.perform(get("/api/v1/post/my-sales-list?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(4))
                .andDo(print());

        //내 거래완료 글 가져오기
        //숨기기를 제외한 거래완료 2개를 가져와야 함
        mockMvc.perform(get("/api/v1/post/my-sales-list?salesState=COMPLETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2))
                .andDo(print());

        //내 숨긴 글 가져오기
        //숨기기글은 거래상태 관계없이 3개를 가져와야 함
        mockMvc.perform(get("/api/v1/post/my-sales-list?isHide=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(3))
                .andDo(print());

    }

    private SalesPostDataForm createTestForm(){
        return SalesPostDataForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.VERY_CLOSE) //최소 탐색 범위
                .build();
    }

    private void createAndSavePost(SalesPostDataForm form, Member member, int n){
        for (int i = 0; i < n; i++) {
            SalesPost postt = SalesPost.createByFormAndMember(form, member);
            salesPostRepository.save(postt);
        }
    }
}
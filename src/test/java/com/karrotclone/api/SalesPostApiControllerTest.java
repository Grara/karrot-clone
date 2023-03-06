package com.karrotclone.api;

import com.karrotclone.domain.Coordinate;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.RangeStep;
import com.karrotclone.domain.enums.Roles;
import com.karrotclone.dto.CreateSalesPostForm;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:application-test.yml")
class SalesPostApiControllerTest {

    @Autowired
    private SalesPostRepository salesPostRepository;

    @Autowired
    private TempMemberRepository memberRepository;

    private Member user;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        user = memberRepository.findByNickName("user").get(); //임시 멤버
    }


    @Test
    void 기본테스트() throws Exception {

        CreateSalesPostForm form = CreateSalesPostForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.VERY_CLOSE)
                .build();

        SalesPost post = SalesPost.createByForm(form, user);
        post.getImageUrls().add("없음");

        Long id = salesPostRepository.save(post).getId();

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("id", Long.toString(id));

        mockMvc.perform(get("/api/v1/post/" + Long.toString(id))
                        .params(info))
                        .andExpect(status().isOk())
                        .andDo(print());

    }

    @Test
    void 거래글목록_카테고리적용_테스트() throws Exception {
        //조건에 카테고리를 적용했을 때 제대로 되는지 확인

        CreateSalesPostForm form = CreateSalesPostForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.VERY_CLOSE)
                .build();

        for (int i = 0; i < 3; i++) { //BOOK 3개
            SalesPost post = SalesPost.createByForm(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        form.setCategory(Category.BEAUTY); //카테고리 변경

        for (int i = 0; i < 3; i++) { //BEAUTY 3개
            SalesPost post = SalesPost.createByForm(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post?page=0&category=BOOK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(3)) //총 3개의 거래글이 조회되야함
                .andDo(print());
    }

    @Test
    void 거래글목록_거리적용_테스트1() throws Exception {
        //멤버와 거래글 서로 범위에 닿는 상황 테스트

        CreateSalesPostForm form = CreateSalesPostForm.builder()
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
            SalesPost post = SalesPost.createByForm(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2)) //총 2개의 거래글이 조회되야함
                .andDo(print());

    }

    @Test
    void 거래글목록_거리적용_테스트2() throws Exception {
        //멤버만 거래글의 범위에 닿는 상황 테스트

        CreateSalesPostForm form = CreateSalesPostForm.builder()
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
            SalesPost post = SalesPost.createByForm(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2)) //총 2개의 거래글이 조회되야함
                .andDo(print());
    }

    @Test
    void 거래글목록_거리적용_테스트3() throws Exception {
        //거래글만 멤버의 범위에 닿는 상황 테스트
        Member mem = memberRepository.findByNickName("user").get();
        mem.setSearchRange(50000);
        memberRepository.save(mem);

        CreateSalesPostForm form = CreateSalesPostForm.builder()
                .title("아아")
                .category(Category.BOOK)
                .price(10000)
                .isNegoAvailable(false)
                .content("팔아요")
                .rangeStep(RangeStep.VERY_CLOSE) //최대 탐색 범위
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
            SalesPost post = SalesPost.createByForm(form, user);
            post.getImageUrls().add("없음");
            salesPostRepository.save(post);
        }

        mockMvc.perform(get("/api/v1/post?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfElements").value(2)) //총 2개의 거래글이 조회되야함
                .andDo(print());

    }

}
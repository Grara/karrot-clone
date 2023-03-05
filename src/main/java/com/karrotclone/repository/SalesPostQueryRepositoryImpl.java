package com.karrotclone.repository;

import com.karrotclone.domain.Coordinate;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.SalesState;
import com.karrotclone.dto.SalesPostSearchCondition;
import com.karrotclone.dto.SalesPostSimpleDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 거래글과 관련된 복잡한 쿼리를 구현한 클래스입니다.
 *
 * @createdBy 노민준
 * @since 2023-02-26
 */
public class SalesPostQueryRepositoryImpl implements SalesPostQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public SalesPostQueryRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * DB에서 판매자가 최근에 등록한 거래글 리스트를 찾아서 반환합니다. (최대4개) <br>
     * 이미지 url을 페치조인합니다. <br>
     * 인자로 들어온 id와 같은 거래글이거나 거래상태가 완료일 경우는 제외합니다. <br>
     * 결과는 ID 내림차순 정렬 <br>
     *
     * @param member 판매자
     * @param id     중복되면 안되는 id
     * @return 찾아낸 거래글 리스트
     * @since 2023-02-28
     */
    @Override
    public List<SalesPost> findTop4ListBySeller(Member member, Long id) {
        return em.createQuery("SELECT s " +
                        "FROM SalesPost s " +
                        "JOIN FETCH s.imageUrls " + //이미지 url 가져올 때 N+1 방지
                        "WHERE s.id != :id AND s.salesState != :salesState " +
                        "ORDER BY s.id DESC", SalesPost.class)
                .setParameter("id", id)
                .setParameter("salesState", SalesState.COMPLETE)
                .setMaxResults(4)
                .getResultList();
    }

    /**
     * 검색조건과 페이지 정보를 바탕으로 Slice를 생성해서 반환해줍니다.
     * @param member 홈 화면을 사용중인 유저의 엔티티 객체
     * @param condition 검색 조건
     * @param pageable 요청한 페이지 정보
     * @return 생성된 Slice 객체
     * @since 2023-03-03
     * @createdBy 노민준
     */
    @Override
    public Slice<SalesPostSimpleDto> findListWithSlice(Member member, SalesPostSearchCondition condition, Pageable pageable) {

        List<SalesPost> postList = em.createQuery("SELECT s " +
                        "FROM SalesPost s " +
                        "JOIN FETCH s.imageUrls " +
                        createWhereFromCondition(member, condition) +
                        " ORDER BY s.id DESC", SalesPost.class) //최신순으로 정렬
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        //거래글을 DTO로 변환
        List<SalesPostSimpleDto> content = postList.stream().map(SalesPostSimpleDto::new).collect(Collectors.toList());

        SliceImpl<SalesPostSimpleDto> result = new SliceImpl<>(content, pageable, content.size() == pageable.getPageSize());

        return result;

    }

    /**
     * 멤버의 정보와 검색조건을 바탕으로 WHERE문을 생성합니다. 리팩토링 필요
     * @param member 홈 화면을 사용중인 유저의 엔티티 객체
     * @param condition 검색조건
     * @return 완성된 WHERE문
     * @since 2023-03-03
     * @createdBy 노민준
     */
    private String createWhereFromCondition(Member member, SalesPostSearchCondition condition) {

        String memberLaStart = Long.toString(member.getTown().getLatitude() - member.getSearchRange()); //멤버의 위도 - 탐지 거리
        String memberLaEnd = Long.toString(member.getTown().getLatitude() + member.getSearchRange()); //멤버의 위도 + 탐지 거리
        String memberLoStart = Long.toString(member.getTown().getLongitude() - member.getSearchRange()); //멤버의 경도 - 탐지 거리
        String memberLoEnd = Long.toString(member.getTown().getLongitude() + member.getSearchRange()); //멤버의 경도 + 탐지 거리

        String memberLa = Long.toString(member.getTown().getLatitude()); //멤버의 위도
        String memberLo = Long.toString(member.getTown().getLongitude()); //멤버의 경도

        String result = "WHERE " + //조건문 시작

                //거래글의 위도가 멤버의 위도±탐지거리 안에 위치해야함
                "s.tradePlace.latitude BETWEEN " + memberLaStart + " AND " + memberLaEnd +
                " AND " +
                //거래글의 경도가 멤버의 경도±탐지거리 안에 위치해야함
                "s.tradePlace.longitude BETWEEN " + memberLoStart + " AND " + memberLoEnd +
                " AND " +
                //멤버의 위도가 거래글의 위도±탐지거리 안에 위치해야함
                memberLa + " BETWEEN s.tradePlace.latitude - s.openRange AND s.tradePlace.latitude + s.openRange" +
                " AND " +
                //멤버의 경도가 거래글의 경도±탐지거리 안에 위치해야함
                memberLo + " BETWEEN s.tradePlace.longitude - s.openRange AND s.tradePlace.longitude + s.openRange" +
                " AND " +
                "s.salesState != 'COMPLETE'"; //거래완료일 경우 제외

        //검색어를 입력했을 경우
        if (StringUtils.hasText(condition.getTitle())) {
            String titleCondition = "'%" + condition.getTitle() + "%'";
            result += " AND s.title LIKE " + titleCondition; //검색어가 제목에 포함되어있는지 여부 확인
        }

        //카테고리를 선택했을 경우
        if (condition.getCategory() != null) {
            String categoryCondition = "'" + condition.getCategory().toString() + "' ";
            result += " AND s.category = " + categoryCondition; //같은 카테고리인지 확인
        }

        return result;

    }


}

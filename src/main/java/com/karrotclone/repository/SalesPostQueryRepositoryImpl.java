package com.karrotclone.repository;

import com.karrotclone.domain.*;
import com.karrotclone.domain.enums.SalesState;
import com.karrotclone.dto.*;
import com.querydsl.core.types.QList;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.karrotclone.domain.QSalesPost.salesPost;

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
     * 인자로 들어온 id와 같은 거래글이거나 거래상태가 완료일 경우, 숨기기가 활성화된 경우는 제외합니다. <br>
     * 결과는 ID 내림차순 정렬 <br>
     *
     * @param member 판매자
     * @param id     중복되면 안되는 id
     * @return 찾아낸 거래글 리스트
     * @lastModified 2023-04-25 노민준
     */
    @Override
    public List<SalesPost> findTop4ListBySeller(Member member, Long id) {
        return em.createQuery("SELECT DISTINCT s " +
                        "FROM SalesPost s " +
                        "LEFT JOIN FETCH s.imageUrls " + //이미지 url 가져올 때 N+1 방지
                        "WHERE s.id != :id AND s.salesState != :salesState AND s.isHide != true AND s.member = :member " +
                        "ORDER BY s.id DESC", SalesPost.class)
                .setParameter("id", id)
                .setParameter("salesState", SalesState.COMPLETE)
                .setParameter("member", member)
                .setMaxResults(4)
                .getResultList();
    }

    /**
     * 홈화면에서 검색조건과 페이지 정보를 바탕으로 Slice를 생성해서 반환해줍니다.
     * @param member 홈 화면을 사용중인 유저의 엔티티 객체
     * @param condition 검색 조건
     * @param pageable 요청한 페이지 정보
     * @return 거래글 DTO 리스트를 지닌 Slice 객체
     * @lastModified 2023-04-25 노민준
     */
    @Override
    public Slice<SalesPostSimpleDto> findHomeList(Member member, PostHomeSearchCondition condition, Pageable pageable) {

        List<SalesPost> postList = em.createQuery("SELECT DISTINCT s " +
                        "FROM SalesPost s " +
                        "LEFT JOIN FETCH s.imageUrls " +
                        homeListCondition(member, condition) +
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
     * 지정한 판매자의 다른 판매글을 가져옵니다.
     * @param condition 검색 조건
     * @param pageable 페이징 파라미터
     * @return 거래글 DTO 리스트를 지닌 Slice 객체
     * @lastModified 2023-04-25 노민준
     */
    @Override
    public Slice<SalesPostSimpleDto> findAllListBySeller(PostSellerSearchCondition condition, Pageable pageable) {

        List<SalesPost> _content = queryFactory
                .select(salesPost).distinct()
                .from(salesPost)
                .leftJoin(salesPost.imageUrls, Expressions.stringPath("imageUrl")).fetchJoin() //이미지 url 리스트 페치조인
                .leftJoin(salesPost.member, QMember.member).fetchJoin() //판매자 페치 조인
                .where(
                        salesPost.member.email.eq(condition.getEmail()), //판매자명
                        stateEq(condition.getSalesState()), //거래상태
                        salesPost.isHide.eq(false)
                )
                .orderBy(salesPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //select에서 바로 DTO생성하면 각 DTO의 객체 주소값이 달라서 DISTINCT가 제대로 작동하지 않음
        List<SalesPostSimpleDto> content = _content.stream().map(SalesPostSimpleDto::new).collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, content.size() == pageable.getPageSize());

    }

    /**
     * 나의 판매글 목록을 가져옵니다.
     * @param member 현재 불러올 판매글 목록의 판매자
     * @param condition 검색 조건
     * @param pageable 페이징 파라미터
     * @return 거래글 DTO 리스트를 지닌 Slice 객체
     * @lastModified 2023-04-25 노민준
     */
    @Override
    public Slice<SalesPostSimpleDto> findMySalesList(Member member, MySalesSearchCondition condition, Pageable pageable) {
        List<SalesPost> _content = queryFactory
                .select(salesPost).distinct()
                .from(salesPost)
                .leftJoin(salesPost.imageUrls, Expressions.stringPath("imageUrl")).fetchJoin()//이미지url 페치조인
                .leftJoin(salesPost.member, QMember.member).fetchJoin() //판매자 페치조인
                .where(
                        salesPost.member.eq(member),
                        stateEq(condition.getSalesState()),
                        salesPost.isHide.eq(condition.getIsHide())
                )
                .orderBy(salesPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //select에서 바로 DTO생성하면 각 DTO의 객체 주소값이 달라서 DISTINCT가 제대로 작동하지 않음
        List<SalesPostSimpleDto> content = _content.stream().map(SalesPostSimpleDto::new).collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, content.size() == pageable.getPageSize());
    }

    /**
     * 멤버의 정보와 검색조건을 바탕으로 JQPL WHERE문을 생성합니다. 리팩토링 필요
     * @param member 홈 화면을 사용중인 유저의 엔티티 객체
     * @param condition 검색조건
     * @return 완성된 WHERE문
     * @lastModified 2023-03-09 노민준
     */
    private String homeListCondition(Member member, PostHomeSearchCondition condition) {

        Coordinate town = member.getTown();

        String memberLaSearchFrom = Long.toString(town.getLatitude() - member.getSearchRange()); //멤버의 위도 - 탐지 거리
        String memberLaSearchTo = Long.toString(town.getLatitude() + member.getSearchRange()); //멤버의 위도 + 탐지 거리
        String memberLoSearchFrom = Long.toString(town.getLongitude() - member.getSearchRange()); //멤버의 경도 - 탐지 거리
        String memberLoSearchTo = Long.toString(town.getLongitude() + member.getSearchRange()); //멤버의 경도 + 탐지 거리

        String memberLa = Long.toString(member.getTown().getLatitude()); //멤버의 위도
        String memberLo = Long.toString(member.getTown().getLongitude()); //멤버의 경도

        String result = "WHERE " + //조건문 시작

                //거래글의 위도가 멤버의 위도±탐지거리 안에 위치해야함
                "s.tradePlace.latitude BETWEEN " + memberLaSearchFrom + " AND " + memberLaSearchTo +
                " AND " +
                //거래글의 경도가 멤버의 경도±탐지거리 안에 위치해야함
                "s.tradePlace.longitude BETWEEN " + memberLoSearchFrom + " AND " + memberLoSearchTo +
                " AND " +
                //멤버의 위도가 거래글의 위도±탐지거리 안에 위치해야함
                memberLa + " BETWEEN s.tradePlace.latitude - s.openRange AND s.tradePlace.latitude + s.openRange" +
                " AND " +
                //멤버의 경도가 거래글의 경도±탐지거리 안에 위치해야함
                memberLo + " BETWEEN s.tradePlace.longitude - s.openRange AND s.tradePlace.longitude + s.openRange" +
                " AND " +
                "s.salesState != 'COMPLETE' AND s.isHide != true "; //거래완료, 숨기기일 경우 제외

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


    /**
     * 거래글 상태에 따른 조건을 생성해줍니다. (쿼리DSL 사용 용도)
     * @param state 지정할 거래글 상태 조건
     * @lastModified 2023-03-09 노민준
     */
    private BooleanExpression stateEq(SalesState state){
        if(state == null) return null; //거래상태가 조건으로 지정안되어있으면 조건무시

        else if(state == SalesState.COMPLETE){ //거래완료일 경우
            return salesPost.salesState.eq(state);
        }

        //아니면 판매중or예약중
        else return salesPost.salesState.eq(SalesState.DEFAULT).or(salesPost.salesState.eq(SalesState.RESERVE));
    }

}

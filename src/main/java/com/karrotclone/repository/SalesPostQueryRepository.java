package com.karrotclone.repository;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.dto.MySalesSearchCondition;
import com.karrotclone.dto.PostHomeSearchCondition;
import com.karrotclone.dto.PostSellerSearchCondition;
import com.karrotclone.dto.SalesPostSimpleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

/**
 * 거래글과 관련된 복잡한 쿼리를 사용하기 위한 인터페이스입니다.
 */
public interface SalesPostQueryRepository {

    /**
     * DB에서 판매자가 최근에 등록한 거래글 리스트를 찾아서 반환합니다. (최대4개)
     * 인자로 들어온 id와 같은 거래글이거나 거래상태가 완료, 숨기기일 경우는 제외합니다.
     * @param member 판매자
     * @param id 중복되면 안되는 id
     * @return 찾아낸 거래글 리스트
     * @since 2023-02-26
     * @lastModified 2023-03-07
     */
    List<SalesPost> findTop4ListBySeller(Member member, Long id);

    /**
     * 검색조건과 페이지 정보를 바탕으로 Slice를 생성해서 반환해줍니다.
     * @param member 홈 화면을 사용중인 유저의 엔티티 객체
     * @param condition 검색 조건
     * @param pageable 요청한 페이지 정보
     * @return 생성된 Slice 객체
     * @since 2023-03-03
     * @createdBy 노민준
     */
    Slice<SalesPostSimpleDto> findListWithSlice(Member member, PostHomeSearchCondition condition, Pageable pageable);

    Slice<SalesPostSimpleDto> findAllListBySeller(PostSellerSearchCondition condition, Pageable pageable);

    Slice<SalesPostSimpleDto> findMySalesList(Member member, MySalesSearchCondition condition, Pageable pageable);
}

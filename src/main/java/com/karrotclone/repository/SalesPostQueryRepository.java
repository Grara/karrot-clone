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
 * 거래글과 관련된 복잡한 쿼리를 사용하기 위한 인터페이스입니다. <br>
 * 메소드의 실질적인 구현은 SalesPostQueryRepositoryImpl 참조
 * @see SalesPostQueryRepositoryImpl
 */
public interface SalesPostQueryRepository {

    List<SalesPost> findTop4ListBySeller(Member member, Long id);

    Slice<SalesPostSimpleDto> findHomeList(Member member, PostHomeSearchCondition condition, Pageable pageable);

    Slice<SalesPostSimpleDto> findAllListBySeller(PostSellerSearchCondition condition, Pageable pageable);

    Slice<SalesPostSimpleDto> findMySalesList(Member member, MySalesSearchCondition condition, Pageable pageable);
}

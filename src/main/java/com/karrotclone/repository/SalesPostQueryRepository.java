package com.karrotclone.repository;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;

import java.util.List;

/**
 * 거래글과 관련된 복잡한 쿼리를 사용하기 위한 인터페이스입니다.
 */
public interface SalesPostQueryRepository {

    /**
     * DB에서 판매자가 최근에 등록한 거래글 리스트를 찾아서 반환합니다. (최대4개)
     * 인자로 들어온 id와 같은 거래글이거나 거래상태가 완료일 경우는 제외합니다.
     * @param member 판매자
     * @param id 중복되면 안되는 id
     * @return 찾아낸 거래글 리스트
     * @since 2023-02-26
     */
    List<SalesPost> findTop4ListBySeller(Member member, Long id);
}

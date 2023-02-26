package com.karrotclone.repository;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 거래글용 DAO입니다
 * @since 2023-02-23
 * @createdBy 노민준
 */
public interface SalesPostRepository extends JpaRepository<SalesPost, Long> {

    /**
     * 판매자가 올린 거래글 최신순으로 최대 4개 가져옵니다.
     * 이때 주어진 id와 일치하는 거래글은 가져오지않습니다.
     * 각 판매글의 멤버를 페치조인해서 가져옵니다.
     * @param member 판매자
     * @param id 겹치지않게 할 거래글 id값
     * @return 가져온 거래글 List
     * @since 2023-02-24
     * @createdBy 노민준
     * @lastModified 2023-02-24
     */
    List<SalesPost> findDistinctTop4ByMemberAndIdNotOrderByIdDesc(Member member, Long id);

}

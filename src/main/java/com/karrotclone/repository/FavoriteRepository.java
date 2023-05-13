package com.karrotclone.repository;

import com.karrotclone.domain.Favorite;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findListByPostAndMember(SalesPost post, Member member);

    /**
     * 멤버의 관심목록을 가져옵니다. 숨기기인 거래글은 제외합니다.
     * @lastModified 2023-04-25
     */
    @Query("SELECT DISTINCT f " +
            "FROM Favorite f " +
            "LEFT JOIN FETCH f.post p " +
            "LEFT JOIN FETCH p.imageUrls " +
            "WHERE f.member = :member AND p.isHide != true " +
            "ORDER BY f.id DESC")
    List<Favorite> findListByMember(@Param("member") Member member);
}

package com.karrotclone.repository;

import com.karrotclone.domain.Favorite;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findListByPostAndMember(SalesPost post, Member member);

    /**
     * 멤버의 관심목록을 가져옵니다. 숨기기인 거래글은 제외합니다.
     * @since 2023-03-07
     */
    @Query("SELECT f " +
            "FROM Favorite f " +
            "JOIN FETCH f.post p " +
            "JOIN FETCH p.imageUrls " +
            "WHERE f.member = :member AND p.isHide != true")
    List<Favorite> findListByMember(@Param("member") Member member);
}

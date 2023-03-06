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

    @Query("SELECT f FROM Favorite f JOIN FETCH f.post p JOIN FETCH p.imageUrls WHERE f.member = :member")
    List<Favorite> findListByMember(@Param("member") Member member);
}

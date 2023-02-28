package com.karrotclone.repository;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 거래글용 DAO입니다
 * @since 2023-02-23
 * @createdBy 노민준
 */
public interface SalesPostRepository extends JpaRepository<SalesPost, Long>, SalesPostQueryRepository {

}

package com.karrotclone.repository;

import com.karrotclone.domain.SalesPost;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 거래글용 DAO입니다
 */
public interface SalesPostRepository extends JpaRepository<SalesPost, Long> {
}

package com.karrotclone.repository;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.SalesState;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * 거래글과 관련된 복잡한 쿼리를 구현한 클래스입니다.
 * @since 2023-02-26
 * @createdBy 노민준
 */
public class SalesPostQueryRepositoryImpl implements SalesPostQueryRepository{

    private final EntityManager em;

    public SalesPostQueryRepositoryImpl(EntityManager em){ this.em = em;}

    /**
     * DB에서 판매자가 최근에 등록한 거래글 리스트를 찾아서 반환합니다. (최대4개) <br>
     * 이미지 url을 페치조인합니다. <br>
     * 인자로 들어온 id와 같은 거래글이거나 거래상태가 완료일 경우는 제외합니다. <br>
     * 결과는 ID 내림차순 정렬 <br>
     * @param member 판매자
     * @param id 중복되면 안되는 id
     * @return 찾아낸 거래글 리스트
     * @since 2023-02-28
     */
    @Override
    public List<SalesPost> findTop4ListBySeller(Member member, Long id) {
        return em.createQuery("SELECT s " +
                        "FROM SalesPost s " +
                        "JOIN FETCH s.imageUrls " + //이미지 url 가져올 때 N+1 방지
                        "WHERE s.id != :id AND s.salesState != :salesState " +
                        "ORDER BY s.id DESC", SalesPost.class)
                .setParameter("id", id)
                .setParameter("salesState", SalesState.COMPLETE)
                .setMaxResults(4)
                .getResultList();
    }
}

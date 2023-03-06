package com.karrotclone.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Favorite {

    @Id @GeneratedValue
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //단방향 다대일
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) //단방향 다대일
    @JoinColumn(name = "sales_post_id")
    private SalesPost post;
}

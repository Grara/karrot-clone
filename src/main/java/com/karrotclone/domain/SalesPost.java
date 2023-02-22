package com.karrotclone.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class SalesPost {

    @Id @GeneratedValue
    @Column(name = "sales_post_id")
    private Long id;
    private long price;
    private String title;
    private Category category; //진행중
}

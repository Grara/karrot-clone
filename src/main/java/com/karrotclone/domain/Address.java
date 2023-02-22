package com.karrotclone.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class Address {
    private String baseAddress;
    private String detailAddress;

    public Address(String baseAddress, String detailAddress) {
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }
}

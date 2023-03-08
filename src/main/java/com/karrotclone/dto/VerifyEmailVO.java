package com.karrotclone.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerifyEmailVO {
    private String key;
    private String email;
}

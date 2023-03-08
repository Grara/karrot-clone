package com.karrotclone.dto;

import com.karrotclone.domain.Coordinate;

import javax.validation.constraints.*;

public class RegisterDto {

    @Null
    private Long id;

    @NotEmpty(message = "이름을 입력해야 합니다.")
    public String nickName;

    @Email(message = "올바르지 않은 양식입니다.")
    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")
    @NotNull
    public String email;

    public String password;

    public Coordinate town;
}

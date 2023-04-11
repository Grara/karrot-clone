package com.karrotclone.dto;

import com.karrotclone.domain.Coordinate;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Data
public class RegisterDto {

    @Null(message = "회원 가입 시 id는 입력하면 안됩니다.")
    private Long id;

    private MultipartFile profile;

    @NotBlank(message = "닉네임은 필수입니다.")
    public String nickName;

    @Email(message = "올바르지 않은 이메일 양식입니다.")
    @NotNull(message = "이메일은 필수입니다.")
    public String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    public String password;

    @NotNull(message = "위치정보는 필수입니다.")
    @Valid
    public Coordinate town;
}

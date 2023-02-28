package com.karrotclone.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ImageTestDto {
    private List<MultipartFile> images;
    private String name;
}

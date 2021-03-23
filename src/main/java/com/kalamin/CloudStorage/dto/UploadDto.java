package com.kalamin.CloudStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UploadDto {
    private long id;
    private List<MultipartFile> files;
    private String folder;
}

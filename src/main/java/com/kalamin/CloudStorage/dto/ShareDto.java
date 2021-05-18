package com.kalamin.CloudStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShareDto {
    private String userId;
    private long fileId;
    private boolean isFolder;

}

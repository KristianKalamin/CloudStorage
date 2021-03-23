package com.kalamin.CloudStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewFolderDto {
    private String folderName;
    private long parentFolderId;
}

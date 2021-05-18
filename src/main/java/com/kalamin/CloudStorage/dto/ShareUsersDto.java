package com.kalamin.CloudStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ShareUsersDto {
    private String userId;
    private long fileId;
    private boolean isFolder;
    private List<String> emails;
}

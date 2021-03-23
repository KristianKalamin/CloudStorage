package com.kalamin.CloudStorage.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FolderDto {
    private long id;
    private String name;
    private Timestamp timeOfCreation;
}

package com.kalamin.CloudStorage.dto;

import com.kalamin.CloudStorage.model.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class FileDto {
    private long id;
    private long size;
    private String name;
    private Timestamp timeOfCreation;
    private int version;
    private Timestamp timeOfDeletion;

    public FileDto(File file) {
        this.id = file.getId();
        this.name = file.getName();
        this.version = file.getVersion();
        this.timeOfCreation = file.getTimeOfCreation();
        this.timeOfDeletion = file.getTimeOfDeletion();
        this.size = file.getSize();
    }
}

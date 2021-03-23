package com.kalamin.CloudStorage.dto;

import com.kalamin.CloudStorage.model.File;
import com.kalamin.CloudStorage.model.Folder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CombinedFilesDto {
    private long id;
    private long size;
    private String name;
    private Timestamp timeOfCreation;
    private Timestamp timeOfDeletion;
    private int version; // if version is -1 then file is folder

    public CombinedFilesDto(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.timeOfCreation = folder.getTimeOfCreation();
        this.timeOfDeletion = folder.getTimeOfDeletion();
        this.size = 0L;
        this.version = -1;
    }

    public CombinedFilesDto(File file) {
        this.id = file.getId();
        this.name = file.getName();
        this.timeOfCreation = file.getTimeOfCreation();
        this.timeOfDeletion = file.getTimeOfDeletion();
        this.size = file.getSize();
        this.version = file.getVersion();
    }
}

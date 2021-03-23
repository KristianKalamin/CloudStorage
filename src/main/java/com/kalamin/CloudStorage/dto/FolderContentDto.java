package com.kalamin.CloudStorage.dto;

import com.kalamin.CloudStorage.model.File;
import com.kalamin.CloudStorage.model.Folder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FolderContentDto {
    private FolderDto rootFolder;
    private List<CombinedFilesDto> combinedFiles;

    public FolderContentDto(@Nullable Folder rootFolder, List<Folder> folders, List<File> files) {
        if (rootFolder == null)
            this.rootFolder = null;
        else
            this.rootFolder = new FolderDto(rootFolder.getId(), rootFolder.getName(), rootFolder.getTimeOfCreation());

        combinedFiles = new ArrayList<>(folders.size() + files.size());

        folders.forEach(f -> combinedFiles.add(new CombinedFilesDto(f)));
        files.forEach(f -> combinedFiles.add(new CombinedFilesDto(f)));
    }
}

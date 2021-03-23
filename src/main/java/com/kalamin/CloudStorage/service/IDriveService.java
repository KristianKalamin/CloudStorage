package com.kalamin.CloudStorage.service;

import com.kalamin.CloudStorage.dto.DeleteDto;
import com.kalamin.CloudStorage.dto.FileDto;
import com.kalamin.CloudStorage.dto.FolderContentDto;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;

public interface IDriveService extends IService {
    FileDto upload(long userId, long folderId, MultipartFile file) throws Exception;

    @Transactional
    void delete(long id, boolean isFolder);

    @Transactional
    FileDto update(long userId, long folderId, MultipartFile file);

    FolderContentDto trash(long userId);

    void createFolder(long userId, String folderName);

    FolderContentDto loadRootFolder(long userId);

    FolderContentDto folderContent(long folderId);

    Path getDownloadPath(long fileId, boolean isFolder) throws IOException;

    void restore(long id, boolean isFolder);
}

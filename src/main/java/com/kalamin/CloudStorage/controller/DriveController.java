package com.kalamin.CloudStorage.controller;

import com.kalamin.CloudStorage.dto.DeleteDto;
import com.kalamin.CloudStorage.dto.FileDto;
import com.kalamin.CloudStorage.dto.FolderContentDto;
import com.kalamin.CloudStorage.dto.NewFolderDto;
import com.kalamin.CloudStorage.service.impl.DriveServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@CrossOrigin
@RestController
@RequestMapping("drive")
public class DriveController {
    @Autowired
    private DriveServiceImpl driveService;

    @GetMapping(value = "files", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> getFile(@RequestParam long id, @RequestParam boolean isFolder) throws IOException {
        final var downloadPath = driveService.getDownloadPath(id, isFolder);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(downloadPath));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("File-Name", downloadPath.getFileName().toString());

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .contentLength(downloadPath.toFile().length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("get-root-folder")
    public @ResponseBody
    ResponseEntity<FolderContentDto> getRootFolder(@RequestBody long userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    driveService.loadRootFolder(userId)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("folder-content")
    public @ResponseBody
    ResponseEntity<FolderContentDto> getFolderContent(@RequestBody long folderId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    driveService.folderContent(folderId)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("new-folder")
    public @ResponseBody
    void createFolder(@RequestBody @Valid NewFolderDto newFolderDto) {
        if (newFolderDto.getFolderName().trim().equals(""))
            return;

        driveService.createFolder(newFolderDto.getParentFolderId(), newFolderDto.getFolderName().trim());
    }

    @PostMapping("update")
    public @ResponseBody
    ResponseEntity<FileDto> update(@RequestParam("file") MultipartFile file,
                                   @RequestParam("folder") long folderId,
                                   @RequestParam("id") long userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    driveService.update(userId, folderId, file));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("upload")
    public @ResponseBody
    ResponseEntity<FileDto> upload(@RequestParam("folder") long folderId,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("id") long userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    driveService.upload(userId, folderId, file));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("trash")
    public @ResponseBody
    ResponseEntity<FolderContentDto> trashedFiles(@RequestBody long userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(driveService.trash(userId));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("delete")
    public @ResponseBody
    void delete(@RequestBody @Valid DeleteDto deleteDto) {
        driveService.delete(deleteDto.getId(), deleteDto.isFolder());
    }

    @PostMapping("restore-deleted")
    public @ResponseBody
    void restore(@RequestBody @Valid DeleteDto deleteDto) {
        driveService.restore(deleteDto.getId(), deleteDto.isFolder());
    }
}



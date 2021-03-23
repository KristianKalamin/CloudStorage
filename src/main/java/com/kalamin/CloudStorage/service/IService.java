package com.kalamin.CloudStorage.service;

import org.springframework.core.io.ClassPathResource;

public interface IService {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    default String createFolderInUploads(String folderName) {
        ClassPathResource res = new ClassPathResource("uploads");
        java.io.File test = new java.io.File(res.getPath());
        final var userFolder = new java.io.File(test + "/" + folderName);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        return userFolder.getPath();
    }
}

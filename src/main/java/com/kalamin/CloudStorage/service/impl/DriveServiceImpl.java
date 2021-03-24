package com.kalamin.CloudStorage.service.impl;

import com.kalamin.CloudStorage.dto.FileDto;
import com.kalamin.CloudStorage.dto.FolderContentDto;
import com.kalamin.CloudStorage.model.Drive;
import com.kalamin.CloudStorage.model.File;
import com.kalamin.CloudStorage.model.Folder;
import com.kalamin.CloudStorage.repository.DriveRepository;
import com.kalamin.CloudStorage.repository.FileRepository;
import com.kalamin.CloudStorage.repository.FolderRepository;
import com.kalamin.CloudStorage.repository.UserRepository;
import com.kalamin.CloudStorage.service.IDriveService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Service
public class DriveServiceImpl implements IDriveService {
    @Autowired
    private DriveRepository driveRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Transactional
    @Override
    public void createFolder(long parentFolderId, String folderName) {
        var parentFolder = folderRepository.getOne(parentFolderId);
        String folderPath = parentFolder.getPath() + "/" + folderName;
        var newFolder = new Folder(folderName, folderPath, new Timestamp(System.currentTimeMillis()), null, parentFolder.getDrive());
        parentFolder.addSubFolder(newFolder);

        java.io.File folder = new java.io.File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        folderRepository.save(newFolder);
    }

    @Override
    public FolderContentDto loadRootFolder(long userId) {
        final var drive = userRepository.getOne(userId).getDrive();
        var folders = drive.getFolders().stream().filter(f -> f.getTimeOfDeletion() == null).collect(Collectors.toList());
        Folder rootFolder = new Folder(folders.stream().filter(f -> f.getName().equals("root")).findFirst().get());
        final var files = rootFolder.getFiles().stream().filter(f -> f.getTimeOfDeletion() == null).collect(Collectors.toList());
        folders.remove(rootFolder);
        rootFolder.getSubFolders().remove(rootFolder);
        folders = folders.stream().filter(f->f.getParentFolder().getId() == rootFolder.getId()).collect(Collectors.toList());

        return new FolderContentDto(rootFolder, folders, files);
    }

    @Override
    public Path getDownloadPath(long id, boolean isFolder) throws IOException {
        if (isFolder) {
            ClassPathResource res = new ClassPathResource("uploads");
            final var folder = folderRepository.getOne(id);
            java.io.File compressed = new java.io.File(res.getPath() + "/" + folder.getName() + ".zip");
            FileOutputStream fos = new FileOutputStream(compressed);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            ZipDirectory.zip(new java.io.File(folder.getPath()), folder.getName(), zipOut);

            zipOut.close();
            fos.close();
            return compressed.getAbsoluteFile().toPath();
        } else {
            return Paths.get(fileRepository.getOne(id).getPath());
        }

    }

    @Override
    public FileDto upload(long userId, long folderId, MultipartFile file) {
        try {
            Drive userDrive = driveRepository.getOne(userId);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            final var rootFolder = userDrive.getFolders().stream().filter(f -> f.getId() == folderId).findFirst().get();
            var newFolder = new java.io.File(rootFolder.getPath());

            final var userFolderFile = new java.io.File(newFolder + "/" + file.getOriginalFilename());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, userFolderFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            File newFile = new File(userFolderFile.length(),
                    userFolderFile.getName(),
                    userFolderFile.getPath(),
                    timestamp,
                    1,
                    null,
                    rootFolder);

            rootFolder.getFiles().add(newFile);

            final var drive = driveRepository.saveAndFlush(userDrive);

            final var updatedFolder = drive.getFolders().stream().filter(folder1 -> folder1.equals(rootFolder)).collect(Collectors.toList()).get(0);

            return new FileDto(updatedFolder.getFiles().stream().filter(file1 -> file1.equals(newFile)).collect(Collectors.toList()).get(0));
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void delete(Folder folder) {
        Timestamp timeOfDeletion = new Timestamp(System.currentTimeMillis());
        folder.setTimeOfDeletion(timeOfDeletion);

        for (File f : folder.getFiles()) {
            final var file = fileRepository.getByIdEquals(f.getId());
            file.setTimeOfDeletion(timeOfDeletion);
        }

        folderRepository.save(folder);
    }

    @Transactional
    @Override
    public void delete(long id, boolean isFolder) {
        if (isFolder) {
            delete(folderRepository.getOne(id));
        } else {
            final var savedFile = fileRepository.getOne(id);
            savedFile.setTimeOfDeletion(new Timestamp(System.currentTimeMillis()));
            fileRepository.save(savedFile);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    @Transactional
    @Override
    public FileDto update(long userId, long folderId, MultipartFile file) {
        final var userFolders = userRepository.getOne(userId).getDrive().getFolders();
        final var folder = userFolders.stream().findFirst().filter(f -> f.getId() == folderId).get();
        final var oldFile = folder.getFiles().stream().findFirst().filter(f -> f.getName().equals(file.getOriginalFilename())).orElse(null);

        oldFile.setVersion(oldFile.getVersion() + 1);
        oldFile.setSize(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Path.of(oldFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
        }
        folderRepository.save(folder);

        return new FileDto(oldFile);
    }

    @Override
    public FolderContentDto trash(long userId) {
        final var trashedFolders = folderRepository.getUsersDeletedFolders(userId);
        final var trashedFiles = fileRepository.getUsersDeletedFiles(userId);
        return new FolderContentDto(null, trashedFolders, trashedFiles);
    }

    @Override
    public void restore(long id, boolean isFolder) {
        if (isFolder) {
            final var folder = folderRepository.getOne(id);
            restoreFolder(folder);
        } else {
            final var file = fileRepository.getOne(id);
            file.setTimeOfDeletion(null);
            fileRepository.save(file);
        }
    }

    private void restoreFolder(Folder folder) {
        folder.setTimeOfDeletion(null);

        for (File f : folder.getFiles()) {
            final var file = fileRepository.getByIdEquals(f.getId());
            file.setTimeOfDeletion(null);
        }

        folderRepository.save(folder);
    }

    @Override
    public FolderContentDto folderContent(long folderId) {
        final var folder = folderRepository.getOne(folderId);
        var subFolders = folder.getSubFolders().stream().filter(f -> f.getTimeOfDeletion() == null).collect(Collectors.toList());
        subFolders.removeIf(f -> f.getName().equals("root"));

        var folderFiles = folder.getFiles().stream().filter(f -> f.getTimeOfDeletion() == null).collect(Collectors.toList());

        return new FolderContentDto(folder.getParentFolder(), subFolders, folderFiles);
    }
}

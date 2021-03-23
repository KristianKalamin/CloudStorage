package com.kalamin.CloudStorage;

import com.kalamin.CloudStorage.model.Folder;
import com.kalamin.CloudStorage.model.User;
import com.kalamin.CloudStorage.repository.FileRepository;
import com.kalamin.CloudStorage.repository.FolderRepository;
import com.kalamin.CloudStorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.Timestamp;

@Component
public class DeleteScheduler {
    private static final int seconds = 120;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    private Timestamp currentTimeStamp;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Scheduled(cron = "0 0/2 * * * ?") // start every 2min forever
    @Transactional
    public void deletePermanently() {
        currentTimeStamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Deleting...");

        var filesForDeletion = fileRepository.getAllByTimeOfDeletionNotNull();
        var foldersForDeletion = folderRepository.getAllByTimeOfDeletionNotNull();

        filesForDeletion.forEach(f -> {
            if (f.getTimeOfDeletion() != null && (currentTimeStamp.getTime() - f.getTimeOfDeletion().getTime()) > seconds * 1000) {
                new File(f.getPath()).delete();
                fileRepository.deleteById(f.getId());
            }
        });

        foldersForDeletion.forEach(f -> {
            if (f.getTimeOfDeletion() != null && (currentTimeStamp.getTime() - f.getTimeOfDeletion().getTime()) > seconds * 1000) {
                FileSystemUtils.deleteRecursively(new File(f.getPath()));
                folderRepository.deleteById(f.getId());

            }
        });
        fileRepository.flush();
        folderRepository.flush();
    }
}

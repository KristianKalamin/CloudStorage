package com.kalamin.CloudStorage.repository;

import com.kalamin.CloudStorage.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File getByIdEquals(long id);
    List<File> getAllByTimeOfDeletionNotNull();
    File getFileByPathEquals(String path);

    @Query("select f from File f WHERE f.folder.drive.user.id =?1 AND f.timeOfDeletion IS NOT NULL")
    List<File> getUsersDeletedFiles(long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM file WHERE file_id = ?1", nativeQuery = true)
    void deleteById(long postId);
}

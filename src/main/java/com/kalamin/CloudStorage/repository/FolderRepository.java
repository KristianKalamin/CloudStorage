package com.kalamin.CloudStorage.repository;

import com.kalamin.CloudStorage.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> getAllByTimeOfDeletionNotNull();

    Folder getFolderByPathEquals(String path);

    @Query("select f from Folder f WHERE f.drive.user.id =?1 AND f.timeOfDeletion IS NOT NULL")
    List<Folder> getUsersDeletedFolders(long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM folder WHERE folder_id = ?1", nativeQuery = true)
    void deleteById(long postId);
}

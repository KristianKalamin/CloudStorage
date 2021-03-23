package com.kalamin.CloudStorage.repository;

import com.kalamin.CloudStorage.model.Drive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriveRepository extends JpaRepository<Drive, Long> {

}

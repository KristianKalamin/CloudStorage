package com.kalamin.CloudStorage.repository;

import com.kalamin.CloudStorage.model.Shared;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepository extends JpaRepository<Shared, Long> {
    Shared findBySharedUriEquals(String uri);
    Shared findSharedByFilePathEquals(String path);
}

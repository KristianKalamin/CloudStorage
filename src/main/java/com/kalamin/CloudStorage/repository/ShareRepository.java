package com.kalamin.CloudStorage.repository;

import com.kalamin.CloudStorage.model.Shared;
import com.kalamin.CloudStorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareRepository extends JpaRepository<Shared, Long> {
    Shared findBySharedUriEquals(String uri);
    Shared findSharedByFilePathEquals(String path);
    List<Shared> findAllByUsersEquals(User userExample);
}

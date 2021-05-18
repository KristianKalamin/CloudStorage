package com.kalamin.CloudStorage.repository;

import com.kalamin.CloudStorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserByIdEquals(String id);
    User findUserByEmailEquals(String email);
}
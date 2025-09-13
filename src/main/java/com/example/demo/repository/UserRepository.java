package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 查詢是否已註冊過
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // ← 加這行
}

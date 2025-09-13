package com.example.demo.repository;

import com.example.demo.entity.Analyst;
import com.example.demo.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalystRepository extends JpaRepository<Analyst, Integer> {
    // 也可以加上其他查詢方法：
    
    Optional<Analyst> findByUser(User user);
    Optional<Analyst> findByUserId(Integer userId);
    List<Analyst> findByVerifiedTrue();

}

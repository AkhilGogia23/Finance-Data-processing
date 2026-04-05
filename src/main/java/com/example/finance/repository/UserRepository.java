package com.example.finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance.entity.UserStatus;
import com.example.finance.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    boolean existsByEmail(String email);
    Optional<Users> findByEmailIgnoreCase(String email);
    Optional<Users> findByEmailIgnoreCaseAndDeletedFalse(String email);
    Optional<Users> findByIdAndDeletedFalse(Long id);
    List<Users> findAllByDeletedFalse();
    List<Users> findAllByDeletedFalseAndStatus(UserStatus status);
    List<Users> findAllByDeletedTrue();
    
}

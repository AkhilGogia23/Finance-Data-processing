package com.example.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

}

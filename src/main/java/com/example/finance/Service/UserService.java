package com.example.finance.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.finance.entity.Users;
import com.example.finance.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Users create(Users user) {
        return repo.save(user);
    }

    public List<Users> getAll() {
        return repo.findAll();
    }

    public void deactivate(Long id) {
        Users user = repo.findById(id).orElseThrow();
        user.setStatus(com.example.finance.entity.UserStatus.INACTIVE);
        repo.save(user);
    }
}
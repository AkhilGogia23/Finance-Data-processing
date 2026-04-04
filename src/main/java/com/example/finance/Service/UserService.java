package com.example.finance.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.finance.entity.Role;
import com.example.finance.entity.Users;
import com.example.finance.exception.BadRequestException;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Users create(Users user) {

        if (repo.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        return repo.save(user);
    }

    public List<Users> getAll() {
        return repo.findAll();
    }

    public void deactivate(Long id) {
        Users user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(com.example.finance.entity.UserStatus.INACTIVE);
        repo.save(user);
    }

    public Users updateRole(Long id, String role) {

        Users user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        try {
            Role newRole = Role.valueOf(role.toUpperCase());
            user.setRole(newRole);
        } catch (Exception e) {
            throw new BadRequestException("Invalid role value");
        }

        return repo.save(user);
    }
}
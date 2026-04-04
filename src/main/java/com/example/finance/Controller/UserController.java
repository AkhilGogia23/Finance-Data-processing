package com.example.finance.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.finance.entity.Users;

import jakarta.validation.Valid;

import com.example.finance.Service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Users user) {
        return ResponseEntity.ok(service.create(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestParam String role) {

        return ResponseEntity.ok(service.updateRole(id, role));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")    
    public List<Users> getAll() {
        return service.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.ok("User deactivated");
    }

    // active users
}
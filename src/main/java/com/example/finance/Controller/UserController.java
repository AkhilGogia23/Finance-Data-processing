package com.example.finance.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.Service.UserService;
import com.example.finance.entity.Users;

import jakarta.validation.Valid;

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

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    public List<Users> getInactiveUsers() {
        return service.getInactiveUsers();
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    public List<Users> getDeletedUsers() {
        return service.getDeletedUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.ok("User deactivated");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivate(@PathVariable Long id) {
        service.reactivate(id);
        return ResponseEntity.ok("User reactivated");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok("User soft deleted");
    }

    // active users
}
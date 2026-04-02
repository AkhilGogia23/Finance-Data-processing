package com.example.finance.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.finance.entity.Users;
import com.example.finance.Service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Users user) {
        return ResponseEntity.ok(service.create(user));
    }

    @GetMapping
    public List<Users> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.ok("User deactivated");
    }
}
package com.example.finance.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.Service.Dashboardservice;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final Dashboardservice service;

    public DashboardController(Dashboardservice service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST') or hasRole('VIEWER')")
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestParam(required = false) String type) {

        return ResponseEntity.ok(service.getSummary(type));
    }
}

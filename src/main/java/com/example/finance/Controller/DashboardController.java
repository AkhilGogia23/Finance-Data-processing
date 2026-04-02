package com.example.finance.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.Service.Dashboardservice;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private Dashboardservice service;

    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    @GetMapping("/summary")
    public Map<String, Double> getSummary() {
        return service.getSummary();
    }
}

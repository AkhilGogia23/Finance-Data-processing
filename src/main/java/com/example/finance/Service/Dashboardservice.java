package com.example.finance.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.entity.Users;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.FinancialRecordRepository;
import com.example.finance.repository.UserRepository;

@Service
public class Dashboardservice {
    @Autowired
    private FinancialRecordRepository repo;
    @Autowired
    private UserRepository userRepo;
   public Map<String, Double> getSummary(String username, String type) {

    Users user = userRepo.findByEmailIgnoreCase(username.trim())
        .orElseThrow(() ->
            new ResourceNotFoundException("User not found with email: " + username));

    List<FinancialRecord> records = repo.findByUserAndDeletedFalse(user);

    double income = records.stream()
            .filter(r -> r.getType() == Type.INCOME)
            .mapToDouble(FinancialRecord::getAmount)
            .sum();

    double expense = records.stream()
            .filter(r -> r.getType() == Type.EXPENSE)
            .mapToDouble(FinancialRecord::getAmount)
            .sum();

    double net = income - expense;

    Map<String, Double> summary = new HashMap<>();

    if (type == null) {
        summary.put("income", income);
        summary.put("expense", expense);
        summary.put("net", net);
        return summary;
    }

    String[] types = type.split(",");

    for (String t : types) {
        switch (t.trim().toLowerCase()) {
            case "income":
                summary.put("income", income);
                break;
            case "expense":
                summary.put("expense", expense);
                break;
            case "net":
                summary.put("net", net);
                break;
        }
    }

    return summary;
}
}

package com.example.finance.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.exception.BadRequestException;
import com.example.finance.repository.FinancialRecordRepository;

@Service
public class Dashboardservice {
    private final FinancialRecordRepository repo;

    public Dashboardservice(FinancialRecordRepository repo) {
        this.repo = repo;
    }

   public Map<String, Double> getSummary(String type) {

    List<FinancialRecord> records = repo.findByDeletedFalse();

    double totalIncome = records.stream()
            .filter(r -> r.getType() == Type.INCOME)
            .mapToDouble(FinancialRecord::getAmount)
            .sum();

    double totalExpense = records.stream()
            .filter(r -> r.getType() == Type.EXPENSE)
            .mapToDouble(FinancialRecord::getAmount)
            .sum();

    double netBalance = totalIncome - totalExpense;

    Map<String, Double> summary = new HashMap<>();

    if (type == null || type.isBlank()) {
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netBalance", netBalance);
        return summary;
    }

    String[] types = type.split(",");
    Set<String> allowed = Set.of("totalincome", "totalexpense", "netbalance");

    for (String t : types) {
        String normalized = t.trim().toLowerCase();
        if (!allowed.contains(normalized)) {
            throw new BadRequestException("Invalid type: " + t + ". Valid values: totalIncome, totalExpense, netBalance");
        }

        switch (normalized) {
            case "totalincome":
                summary.put("totalIncome", totalIncome);
                break;
            case "totalexpense":
                summary.put("totalExpense", totalExpense);
                break;
            case "netbalance":
                summary.put("netBalance", netBalance);
                break;
        }
    }

    return summary;
}
}

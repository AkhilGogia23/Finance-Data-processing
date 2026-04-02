package com.example.finance.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.repository.FinancialRecordRepository;

@Service
public class Dashboardservice {
    @Autowired
    private FinancialRecordRepository repo;

    public Map<String, Double> getSummary() {
        List<FinancialRecord> records = repo.findAll();

        double income = records.stream().filter(r -> r.getType() == Type.INCOME).mapToDouble(FinancialRecord::getAmount)
                .sum();

        double expense = records.stream().filter(r -> r.getType() == Type.EXPENSE)
                .mapToDouble(FinancialRecord::getAmount).sum();

        Map<String, Double> summary = new HashMap<>();
        summary.put("income", income);
        summary.put("expense", expense);
        summary.put("net", income - expense);
        return summary;
    }
}

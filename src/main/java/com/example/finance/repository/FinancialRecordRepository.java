package com.example.finance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    List<FinancialRecord> findByTypeAndCategoryAndDateBetween(
            Type type,
            String category,
            LocalDate startDate,
            LocalDate endDate);
}

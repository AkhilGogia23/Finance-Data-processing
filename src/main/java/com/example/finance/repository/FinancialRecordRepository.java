package com.example.finance.repository;

import java.time.LocalDate;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.entity.Users;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    List<FinancialRecord> findByTypeAndCategoryAndDateBetweenAndDeletedFalse(
            Type type,
            String category,
            LocalDate startDate,
            LocalDate endDate);

    List<FinancialRecord> findByDeletedFalse();
        List<FinancialRecord> findByUserAndDeletedFalse(Users user);
}


package com.example.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.finance.Service.Dashboardservice;
import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.repository.FinancialRecordRepository;

class DashboardServiceTest {

    @Mock
    private FinancialRecordRepository repo;

    private Dashboardservice service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new Dashboardservice(repo);
    }

    @Test
    void testSummaryCalculation() {

        FinancialRecord r1 = new FinancialRecord();
        r1.setType(Type.INCOME);
        r1.setAmount(1000.0);

        FinancialRecord r2 = new FinancialRecord();
        r2.setType(Type.EXPENSE);
        r2.setAmount(300.0);

        when(repo.findByDeletedFalse())
            .thenReturn(List.of(r1, r2));

        Map<String, Double> result = service.getSummary(null);

        assertEquals(1000.0, result.get("totalIncome"));
        assertEquals(300.0, result.get("totalExpense"));
        assertEquals(700.0, result.get("netBalance"));
    }
}
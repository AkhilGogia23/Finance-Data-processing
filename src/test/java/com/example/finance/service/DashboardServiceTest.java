package com.example.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.example.finance.Service.Dashboardservice;
import com.example.finance.entity.*;
import com.example.finance.repository.*;

class DashboardServiceTest {

    @Mock
    private FinancialRecordRepository repo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private Dashboardservice service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSummaryCalculation() {

        Users user = new Users();
        user.setEmail("akhil@gmail.com");

        when(userRepo.findByEmailIgnoreCase("akhil@gmail.com"))
                .thenReturn(Optional.of(user));

        FinancialRecord r1 = new FinancialRecord();
        r1.setType(Type.INCOME);
        r1.setAmount(1000.0);

        FinancialRecord r2 = new FinancialRecord();
        r2.setType(Type.EXPENSE);
        r2.setAmount(300.0);

        when(repo.findByUserAndDeletedFalse(user))
                .thenReturn(List.of(r1, r2));

        Map<String, Double> result = service.getSummary("akhil@gmail.com", null);

        assertEquals(1000.0, result.get("income"));
        assertEquals(300.0, result.get("expense"));
        assertEquals(700.0, result.get("net"));
    }
}
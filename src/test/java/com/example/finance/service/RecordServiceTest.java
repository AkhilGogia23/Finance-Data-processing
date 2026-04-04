package com.example.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.example.finance.Dto.RecordDto;
import com.example.finance.Service.RecordService;
import com.example.finance.entity.*;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.*;

class RecordServiceTest {

    @Mock
    private FinancialRecordRepository repo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private RecordService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST 1: Success
    @Test
    void testCreateRecordSuccess() {

        Users user = new Users();
        user.setEmail("akhil@gmail.com");

        when(userRepo.findByEmailIgnoreCase("akhil@gmail.com"))
                .thenReturn(Optional.of(user));

        RecordDto dto = new RecordDto();
        dto.setAmount(1000.0);
        dto.setType(Type.INCOME);
        dto.setCategory("SALARY");

        FinancialRecord saved = new FinancialRecord();
        when(repo.save(any())).thenReturn(saved);

        FinancialRecord result = service.create(dto, "akhil@gmail.com");

        assertNotNull(result);
        verify(repo, times(1)).save(any());
    }

    // ❌ TEST 2: User Not Found
    @Test
    void testCreateRecordUserNotFound() {

        when(userRepo.findByEmailIgnoreCase("wrong@gmail.com"))
                .thenReturn(Optional.empty());

        RecordDto dto = new RecordDto();

        assertThrows(ResourceNotFoundException.class, () -> {
            service.create(dto, "wrong@gmail.com");
        });
    }
}
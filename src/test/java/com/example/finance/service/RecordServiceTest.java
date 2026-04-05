package com.example.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.finance.Dto.RecordDto;
import com.example.finance.Service.RecordService;
import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.entity.UserStatus;
import com.example.finance.entity.Users;
import com.example.finance.exception.BadRequestException;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.FinancialRecordRepository;
import com.example.finance.repository.UserRepository;

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
        user.setStatus(UserStatus.ACTIVE);

        when(userRepo.findByEmailIgnoreCaseAndDeletedFalse("akhil@gmail.com"))
                .thenReturn(Optional.of(user));

        RecordDto dto = new RecordDto();
        dto.setAmount(1000.0);
        dto.setType(Type.INCOME);
        dto.setCategory("SALARY");
        dto.setUserEmail("akhil@gmail.com");

        FinancialRecord saved = new FinancialRecord();
        when(repo.save(any())).thenReturn(saved);

        FinancialRecord result = service.create(dto, "akhil@gmail.com", false);

        assertNotNull(result);
        verify(repo, times(1)).save(any());
    }

    // ❌ TEST 2: User Not Found
    @Test
    void testCreateRecordUserNotFound() {

        when(userRepo.findByEmailIgnoreCaseAndDeletedFalse("wrong@gmail.com"))
                .thenReturn(Optional.empty());

        RecordDto dto = new RecordDto();
        dto.setAmount(1000.0);
        dto.setType(Type.INCOME);
        dto.setUserEmail("wrong@gmail.com");

        assertThrows(ResourceNotFoundException.class, () -> {
            service.create(dto, "wrong@gmail.com", false);
        });
    }

    @Test
    void testCreateRecordForOtherUserNotAllowedForNonAdmin() {
        RecordDto dto = new RecordDto();
        dto.setAmount(1000.0);
        dto.setType(Type.INCOME);
        dto.setUserEmail("other@gmail.com");

        assertThrows(BadRequestException.class, () -> {
            service.create(dto, "akhil@gmail.com", false);
        });
    }

    @Test
    void testCreateRecordForInactiveTargetUserNotAllowed() {
        Users admin = new Users();
        admin.setEmail("admin@gmail.com");
        admin.setStatus(UserStatus.ACTIVE);

        Users inactiveTarget = new Users();
        inactiveTarget.setEmail("inactive@gmail.com");
        inactiveTarget.setStatus(UserStatus.INACTIVE);

        when(userRepo.findByEmailIgnoreCaseAndDeletedFalse("admin@gmail.com"))
                .thenReturn(Optional.of(admin));
        when(userRepo.findByEmailIgnoreCaseAndDeletedFalse("inactive@gmail.com"))
                .thenReturn(Optional.of(inactiveTarget));

        RecordDto dto = new RecordDto();
        dto.setAmount(1000.0);
        dto.setType(Type.INCOME);
        dto.setUserEmail("inactive@gmail.com");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            service.create(dto, "admin@gmail.com", true);
        });

        assertEquals("Cannot create records for inactive users", ex.getMessage());
    }

    @Test
    void testGetAllRecords() {
        FinancialRecord r1 = new FinancialRecord();
        FinancialRecord r2 = new FinancialRecord();

        when(repo.findByDeletedFalse()).thenReturn(List.of(r1, r2));

        List<FinancialRecord> result = service.getAllRecords();

        assertEquals(2, result.size());
        verify(repo, times(1)).findByDeletedFalse();
    }
}
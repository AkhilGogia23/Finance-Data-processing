package com.example.finance.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finance.Dto.RecordDto;
import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.entity.Users;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.FinancialRecordRepository;
import com.example.finance.repository.UserRepository;

@Service
public class RecordService {
    @Autowired
    private FinancialRecordRepository repo;
    @Autowired
    private UserRepository userRepo;

    public FinancialRecord create(RecordDto dto, String username) {

       Users user = userRepo.findByEmailIgnoreCase(username.trim())
        .orElseThrow(() ->
            new ResourceNotFoundException("User not found with email: " + username));
        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(LocalDate.now());
        record.setUser(user);

        return repo.save(record);
    }

    public List<FinancialRecord> getAll(String username) {

       Users user = userRepo.findByEmailIgnoreCase(username.trim())
        .orElseThrow(() ->
            new ResourceNotFoundException("User not found with email: " + username));

        return repo.findByUserAndDeletedFalse(user);
    }

    public FinancialRecord update(Long id, RecordDto dto) {
        FinancialRecord record = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());

        return repo.save(record);
    }

    public List<FinancialRecord> filter(
            Type type,
            String category,
            LocalDate startDate,
            LocalDate endDate) {

        return repo.findByTypeAndCategoryAndDateBetweenAndDeletedFalse(
                type,
                category,
                startDate,
                endDate);
    }

    public void softDelete(Long id) {
        FinancialRecord record = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setDeleted(true);
        repo.save(record);
    }
}

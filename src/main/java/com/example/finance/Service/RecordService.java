package com.example.finance.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.finance.Dto.RecordDto;
import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.entity.UserStatus;
import com.example.finance.entity.Users;
import com.example.finance.exception.BadRequestException;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.FinancialRecordRepository;
import com.example.finance.repository.UserRepository;

@Service
public class RecordService {
    private final FinancialRecordRepository repo;
    private final UserRepository userRepo;

    public RecordService(FinancialRecordRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public FinancialRecord create(RecordDto dto, String authenticatedUsername, boolean canCreateForOtherUsers) {

        String targetUserEmail = dto.getUserEmail().trim();
        String creatorEmail = authenticatedUsername.trim();

        if (!canCreateForOtherUsers && !targetUserEmail.equalsIgnoreCase(creatorEmail)) {
            throw new BadRequestException("You can only create records for your own account");
        }

        Users creator = userRepo.findByEmailIgnoreCaseAndDeletedFalse(creatorEmail)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found with email: " + creatorEmail));

        Users user = userRepo.findByEmailIgnoreCaseAndDeletedFalse(targetUserEmail)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found with email: " + targetUserEmail));

        if (creator.isDeleted()) {
            throw new BadRequestException("Deleted users cannot create records");
        }

        if (creator.getStatus() == UserStatus.INACTIVE) {
            throw new BadRequestException("Inactive users cannot create records");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BadRequestException("Cannot create records for inactive users");
        }

        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(LocalDate.now());
        record.setUser(user);

        return repo.save(record);
    }

    public List<FinancialRecord> getAll(String username) {

         Users user = userRepo.findByEmailIgnoreCaseAndDeletedFalse(username.trim())
        .orElseThrow(() ->
            new ResourceNotFoundException("User not found with email: " + username));

        return repo.findByUserAndDeletedFalse(user);
    }

    public List<FinancialRecord> getAllRecords() {
        return repo.findByDeletedFalse();
    }

    public List<FinancialRecord> getAllByUserEmail(String userEmail) {
        Users user = userRepo.findByEmailIgnoreCaseAndDeletedFalse(userEmail.trim())
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found with email: " + userEmail));

        return repo.findByUserAndDeletedFalse(user);
    }

    public FinancialRecord update(Long id, RecordDto dto) {
        FinancialRecord record = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        if (record.isDeleted()) {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }

        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());

        return repo.save(record);
    }

    public List<FinancialRecord> filter(
            Type type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            String username) {

        Users user = userRepo.findByEmailIgnoreCaseAndDeletedFalse(username.trim())
            .orElseThrow(() ->
                new ResourceNotFoundException("User not found with email: " + username));

        List<FinancialRecord> records = repo.findByUserAndDeletedFalse(user);

        return records.stream()
            .filter(record -> type == null || record.getType() == type)
            .filter(record -> category == null || category.isBlank() ||
                (record.getCategory() != null && record.getCategory().equalsIgnoreCase(category.trim())))
            .filter(record -> startDate == null ||
                (record.getDate() != null && !record.getDate().isBefore(startDate)))
            .filter(record -> endDate == null ||
                (record.getDate() != null && !record.getDate().isAfter(endDate)))
            .collect(Collectors.toList());
    }

    public void softDelete(Long id) {
        FinancialRecord record = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

        record.setDeleted(true);
        repo.save(record);
    }
}

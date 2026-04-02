package com.example.finance.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.finance.Dto.RecordDto;
import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;
import com.example.finance.repository.FinancialRecordRepository;

@Service
public class RecordService {
    @Autowired
    private FinancialRecordRepository repo;

    public FinancialRecord create(RecordDto dto) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(LocalDate.now());
        return repo.save(record);
    }

    public List<FinancialRecord> getAll() {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<FinancialRecord> filter(
            Type type,
            String category,
            LocalDate startDate,
            LocalDate endDate) {

        if (type == null || category == null || startDate == null || endDate == null) {
            throw new RuntimeException("All filter parameters are required");
        }

        return repo.findByTypeAndCategoryAndDateBetween(
                type,
                category,
                startDate,
                endDate);
    }
}

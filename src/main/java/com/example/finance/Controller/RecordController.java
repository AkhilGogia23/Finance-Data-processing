package com.example.finance.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.Dto.RecordDto;
import com.example.finance.Service.RecordService;
import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;

import jakarta.validation.Valid;

@RestController
@RequestMapping("records")
public class RecordController {
    @Autowired
    private RecordService service;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-USER") String username,
            @Valid @RequestBody RecordDto dto) {

        return ResponseEntity.ok(service.create(dto, username));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping
    public List<FinancialRecord> getAll(@RequestHeader("X-USER") String username) {
        return service.getAll(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok("Record soft deleted");
    }

    // @PutMapping()

    @GetMapping("/filter")
    public ResponseEntity<?> filter(
            @RequestParam(required =false) Type type,
            @RequestParam(required =false) String category,

            @RequestParam(required =false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required =false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            
        ) {
        return ResponseEntity.ok(
                service.filter(type, category, startDate, endDate));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody RecordDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

}

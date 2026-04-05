package com.example.finance.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.Dto.RecordDto;
import com.example.finance.Service.RecordService;
import com.example.finance.entity.FinancialRecord;
import com.example.finance.entity.Type;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/records")
public class RecordController {
    private final RecordService service;

    public RecordController(RecordService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(
            Authentication authentication,
            @Valid @RequestBody RecordDto dto) {

        boolean canCreateForOtherUsers = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_ADMIN"::equals);

        return ResponseEntity.ok(service.create(dto, authentication.getName(), canCreateForOtherUsers));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping
    public List<FinancialRecord> getAll(@RequestParam(required = false) String userEmail) {
        if (userEmail != null && !userEmail.isBlank()) {
            return service.getAllByUserEmail(userEmail);
        }

        return service.getAllRecords();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok("Record soft deleted");
    }

    // @PutMapping()

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/filter")
    public ResponseEntity<?> filter(
            Authentication authentication,
            @RequestParam(required =false) Type type,
            @RequestParam(required =false) String category,

            @RequestParam(required =false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required =false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            
        ) {
        return ResponseEntity.ok(
            service.filter(type, category, startDate, endDate, authentication.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody RecordDto dto) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (!isAdmin) {
            throw new AccessDeniedException("Only ADMIN can update records");
        }

        return ResponseEntity.ok(service.update(id, dto));
    }

}

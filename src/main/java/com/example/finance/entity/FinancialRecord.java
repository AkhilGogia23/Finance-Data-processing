package com.example.finance.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class FinancialRecord {
    @Id
    @GeneratedValue
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String category;

    private LocalDate date;

    private String notes;

    @Column(nullable = false)
    private boolean deleted = false;
    
    @ManyToOne
    private Users createdBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

}

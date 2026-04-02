package com.example.finance.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
// import jakarta.persistence.ManyToOne;
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

    // @ManyToOne
    // private Users createdBy;

}

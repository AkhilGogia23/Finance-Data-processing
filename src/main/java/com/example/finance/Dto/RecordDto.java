package com.example.finance.Dto;

import com.example.finance.entity.Type;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RecordDto {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    @NotNull(message = "Type is required")
    private Type type;

    private String category;
    private String notes;

}

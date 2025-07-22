package org.example.mini_finance_tracker_backend.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDto {

    private String transactionName;
    private Integer amount;
    private String type;
    private LocalDate createdAt;
}

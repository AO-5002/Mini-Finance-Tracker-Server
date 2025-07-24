package org.example.mini_finance_tracker_backend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionDto {

    private UUID id;
    @NotNull(message = "Transaction name is required!")
    private String transactionName;
    @NotNull(message = "Transaction amount is required!")
    private Integer amount;
    @NotNull(message = "Transaction type is required!")
    private String type;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate createdAt;
}

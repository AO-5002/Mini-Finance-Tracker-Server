package org.example.mini_finance_tracker_backend.repositories;

import org.example.mini_finance_tracker_backend.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}

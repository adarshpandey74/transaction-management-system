package com.adarsh.transaction_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adarsh.transaction_system.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

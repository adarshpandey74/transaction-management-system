package com.adarsh.transaction_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adarsh.transaction_system.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
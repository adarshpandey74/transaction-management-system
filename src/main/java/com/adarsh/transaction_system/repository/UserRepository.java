package com.adarsh.transaction_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adarsh.transaction_system.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
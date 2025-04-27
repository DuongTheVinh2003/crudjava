package com.example.CURDjava.responsitory;

import com.example.CURDjava.model.transactions;
import com.example.CURDjava.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<transactions, Long> {
    List<transactions> findByUser(User user);
}

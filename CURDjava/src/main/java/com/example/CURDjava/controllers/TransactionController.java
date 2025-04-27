package com.example.CURDjava.controllers;
import com.example.CURDjava.model.transactions;
import com.example.CURDjava.responsitory.TransactionRepository;
import com.example.CURDjava.model.User;
import com.example.CURDjava.responsitory.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        List<transactions> transactions = transactionRepository.findByUser(user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", calculateBalance(transactions));
        return "transactions";
    }

    @GetMapping("/transactions/new")
    public String showTransactionForm(Model model) {
        model.addAttribute("transaction", new transactions());
        return "transaction-form";
    }

    @PostMapping("/transactions")
    public String saveTransaction(@ModelAttribute transactions transaction) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        transaction.setUser(user);
        transaction.setDate(LocalDate.now());
        transactionRepository.save(transaction);
        return "redirect:/transactions";
    }
    @GetMapping("/transactions/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        transactions transaction = transactionRepository.findById(id).orElseThrow();
        model.addAttribute("transaction", transaction);
        return "transaction-form";
    }

    @PostMapping("/transactions/update/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute transactions transaction) {
        transactions existingTransaction = transactionRepository.findById(id).orElseThrow();
        existingTransaction.setDescription(transaction.getDescription());
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setType(transaction.getType());
        transactionRepository.save(existingTransaction);
        return "redirect:/transactions";
    }

    @GetMapping("/transactions/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionRepository.deleteById(id);
        return "redirect:/transactions";
    }

    private double calculateBalance(List<transactions> transactions) {
        double balance = 0;
        for (  transactions t : transactions) {
            if (t.getType().equals("INCOME")) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
        }
        return balance;
    }
}

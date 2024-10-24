package com.java.bfsi.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.bfsi.dto.TransactionDto;
import com.java.bfsi.exception.SuspendedAccountException;
import com.java.bfsi.model.Transaction;
import com.java.bfsi.service.TransactionService;

@RestController
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> makeTransaction(@PathVariable Long accountId, @RequestBody TransactionDto transactionDto) {
        Transaction transaction = null;
		try {
			transaction = transactionService.makeTransaction(accountId, transactionDto);
		} catch (SuspendedAccountException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Transaction> transactions = transactionService.getTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(transactions);
    }
}
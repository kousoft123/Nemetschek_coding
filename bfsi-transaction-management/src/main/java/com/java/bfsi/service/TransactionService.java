package com.java.bfsi.service;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.bfsi.dto.TransactionDto;
import com.java.bfsi.exception.SuspendedAccountException;
import com.java.bfsi.model.Account;
import com.java.bfsi.model.Transaction;
import com.java.bfsi.repository.AccountRepository;
import com.java.bfsi.repository.TransactionRepository;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final BigDecimal FRAUD_THRESHOLD = new BigDecimal("20000.00");

    @Transactional
    public Transaction makeTransaction(Long accountId, TransactionDto transactionDto) throws SuspendedAccountException {
        Account account = accountRepository.findById(accountId).orElseThrow();
        
        if (account.getSuspended()) {
            throw new SuspendedAccountException("Account is suspended.");
        }
        
        

        BigDecimal newBalance = transactionDto.getType().equals("cr") ?
                account.getBalance().add(transactionDto.getAmount()) :
                account.getBalance().subtract(transactionDto.getAmount());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds.");
        }

        checkFraud(accountId, transactionDto);

        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType("cr");

        return transactionRepository.save(transaction);
    }

    public Page<Transaction> getTransactionHistory(Long accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByAccount_Id(accountId, pageable);
    }

    private void checkFraud(Long accountId, TransactionDto transactionDto) {
        if (transactionDto.getType().equals("dr")) {
            BigDecimal totalWithdrawalsToday = transactionRepository.findByAccount_Id(accountId, PageRequest.of(0, Integer.MAX_VALUE))
                    .getContent().stream()
                    .filter(t -> t.getTimestamp().toLocalDate().isEqual(LocalDate.now()) && t.getType().equals("dr"))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalWithdrawalsToday.add(transactionDto.getAmount()).compareTo(FRAUD_THRESHOLD) > 0) {
                throw new RuntimeException("Fraud alert: withdrawal limit exceeded for the day.");
            }
        }
    }
}
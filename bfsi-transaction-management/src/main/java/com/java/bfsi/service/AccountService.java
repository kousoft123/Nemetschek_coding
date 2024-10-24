package com.java.bfsi.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.bfsi.dto.AccountDto;
import com.java.bfsi.model.Account;
import com.java.bfsi.repository.AccountRepository;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(AccountDto accountDto) {
        Account account = new Account();
        account.setAccountno(accountDto.getAccountno());
        account.setBalance(accountDto.getInitialBalance());
        return accountRepository.save(account);
    }

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow();
    }

    public BigDecimal getAccountBalance(Long accountId) {
        Account account = getAccount(accountId);
        return account.getBalance();
    }

    public void suspendAccount(Long accountId) {
        Account account = getAccount(accountId);
        account.setSuspended(true);
        accountRepository.save(account);
    }

	
}
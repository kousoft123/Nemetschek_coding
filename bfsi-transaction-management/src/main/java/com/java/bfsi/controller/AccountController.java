package com.java.bfsi.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java.bfsi.dto.AccountDto;
import com.java.bfsi.model.Account;
import com.java.bfsi.service.AccountService;

import java.math.BigDecimal;

@EnableSpringDataWebSupport(pageSerializationMode=PageSerializationMode.VIA_DTO)
@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto) {
        Account account = accountService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }
    
    

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountBalance(accountId));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> suspendAccount(@PathVariable Long accountId) {
        accountService.suspendAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
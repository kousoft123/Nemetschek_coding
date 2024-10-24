package com.java.bfsi.accountTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.java.bfsi.controller.AccountController;
import com.java.bfsi.dto.AccountDto;
import com.java.bfsi.model.Account;
import com.java.bfsi.service.AccountService;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountno("123456789");
        account.setBalance(new BigDecimal("5000.00"));
        account.setSuspended(false);

        accountDto = new AccountDto();
        accountDto.setAccountno("123456789");
        accountDto.setInitialBalance(new BigDecimal("5000.00"));
    }

    @Test
    public void testCreateAccount() {
        when(accountService.createAccount(any(AccountDto.class))).thenReturn(account);

        ResponseEntity<Account> response = accountController.createAccount(accountDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(account, response.getBody());
        
        verify(accountService, times(1)).createAccount(any(AccountDto.class));
    }

    @Test
    public void testGetAccount() {
        when(accountService.getAccount(1L)).thenReturn(account);

        ResponseEntity<Account> response = accountController.getAccount(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
        
        verify(accountService, times(1)).getAccount(1L);
    }

    @Test
    public void testGetAccountBalance() {
        BigDecimal balance = new BigDecimal("5000.00");
        when(accountService.getAccountBalance(1L)).thenReturn(balance);

        ResponseEntity<BigDecimal> response = accountController.getAccountBalance(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balance, response.getBody());
        
        verify(accountService, times(1)).getAccountBalance(1L);
    }

    @Test
    public void testSuspendAccount() {
        doNothing().when(accountService).suspendAccount(1L);

        ResponseEntity<Void> response = accountController.suspendAccount(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(accountService, times(1)).suspendAccount(1L);
    }
}
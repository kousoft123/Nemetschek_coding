package com.java.bfsi.accountTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.java.bfsi.dto.AccountDto;
import com.java.bfsi.model.Account;
import com.java.bfsi.repository.AccountRepository;
import com.java.bfsi.service.AccountService;

public class AccountServiceTest {

	  @Mock
	    private AccountRepository accountRepository;

	    @InjectMocks
	    private AccountService accountService;

	    @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    void testCreateAccount() {
	        AccountDto accountDto = new AccountDto();
	        accountDto.setAccountno("123456");
	        accountDto.setInitialBalance(new BigDecimal("5000.00"));

	        Account account = new Account();
	        account.setAccountno(accountDto.getAccountno());
	        account.setBalance(accountDto.getInitialBalance());

	        when(accountRepository.save(any(Account.class))).thenReturn(account);

	        Account createdAccount = accountService.createAccount(accountDto);

	        assertNotNull(createdAccount);
	        assertEquals(accountDto.getAccountno(), createdAccount.getAccountno());
	        assertEquals(accountDto.getInitialBalance(), createdAccount.getBalance());
	        verify(accountRepository, times(1)).save(any(Account.class));
	    }

	    @Test
	    void testGetAccount() {
	        Account account = new Account();
	        account.setId(1L);
	        account.setAccountno("123456");
	        account.setBalance(new BigDecimal("5000.00"));

	        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

	        Account foundAccount = accountService.getAccount(1L);

	        assertNotNull(foundAccount);
	        assertEquals("123456", foundAccount.getAccountno());
	        assertEquals(new BigDecimal("5000.00"), foundAccount.getBalance());
	        verify(accountRepository, times(1)).findById(1L);
	    }

	    @Test
	    void testGetAccountBalance() {
	        Account account = new Account();
	        account.setId(1L);
	        account.setBalance(new BigDecimal("5000.00"));

	        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

	        BigDecimal balance = accountService.getAccountBalance(1L);

	        assertNotNull(balance);
	        assertEquals(new BigDecimal("5000.00"), balance);
	        verify(accountRepository, times(1)).findById(1L);
	    }

	    @Test
	    void testSuspendAccount() {
	        Account account = new Account();
	        account.setId(1L);
	        account.setSuspended(false);

	        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

	        accountService.suspendAccount(1L);

	        assertTrue(account.getSuspended());
	        verify(accountRepository, times(1)).save(account);
	    }

	    @Test
	    void testGetAccount_throwsExceptionWhenNotFound() {
	        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

	        assertThrows(RuntimeException.class, () -> accountService.getAccount(1L));
	        verify(accountRepository, times(1)).findById(1L);
	    }
}
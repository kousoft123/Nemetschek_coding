package com.java.bfsi.transactionTest;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.anyOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.java.bfsi.dto.TransactionDto;
import com.java.bfsi.exception.SuspendedAccountException;
import com.java.bfsi.model.Account;
import com.java.bfsi.model.Transaction;
import com.java.bfsi.repository.AccountRepository;
import com.java.bfsi.repository.TransactionRepository;
import com.java.bfsi.service.TransactionService;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;
    private TransactionDto transactionDto;

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountno("100908977543");
        account.setBalance(new BigDecimal("5000.00"));
        account.setSuspended(false);

        transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("2000.00"));
        transactionDto.setType("dr"); // "cr" for credit, "dr" for debit
    }

    @Test
    public void testMakeTransaction_Success() throws SuspendedAccountException {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        
        // Mocking the transaction repository to return an empty page for today's transactions
        when(transactionRepository.findByAccount_Id(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.Collections.emptyList()));

        // Create a transaction object to return when save is called
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setAccount(account);
        savedTransaction.setAmount(transactionDto.getAmount());
        savedTransaction.setType(transactionDto.getType());

        // Mock the save method to return the saved transaction
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction transaction = transactionService.makeTransaction(1L, transactionDto);
        
        assertNotNull(transaction);
        assertEquals(new BigDecimal("2000.00"), transaction.getAmount());
        assertEquals("dr", transaction.getType());
        assertEquals(new BigDecimal("3000.00"), account.getBalance()); // Balance should be updated

        verify(transactionRepository, times(1)).save(any());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testMakeTransaction_AccountSuspended() {
        account.setSuspended(true);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        
        SuspendedAccountException exception = assertThrows(SuspendedAccountException.class, () -> {
            transactionService.makeTransaction(1L, transactionDto);
        });

        assertEquals("Account is suspended.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void testMakeTransaction_InsufficientFunds() {
        transactionDto.setAmount(new BigDecimal("6000.00")); // Setting a withdrawal greater than the balance
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.makeTransaction(1L, transactionDto);
        });

        assertEquals("Insufficient funds.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void testMakeTransaction_FraudAlert() {
        transactionDto.setType("dr");
        transactionDto.setAmount(new BigDecimal("9000.00")); // Withdrawal that would exceed the threshold
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
      
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.makeTransaction(1L, transactionDto);
        });
       
        verify(transactionRepository, never()).save(any());
    }

    private Transaction createTransaction(BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType("dr");
        transaction.setTimestamp(LocalDateTime.now()); // Set timestamp to now for testing
        return transaction;
    }
}
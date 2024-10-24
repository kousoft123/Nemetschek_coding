package com.java.bfsi.transactionTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.java.bfsi.controller.TransactionController;
import com.java.bfsi.dto.TransactionDto;
import com.java.bfsi.exception.SuspendedAccountException;
import com.java.bfsi.model.Transaction;
import com.java.bfsi.service.TransactionService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction transaction;
    private TransactionDto transactionDto;

    @BeforeEach
    public void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("2000.00"));
        transaction.setType("cr");

        transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("2000.00"));
        transactionDto.setType("cr");
    }

    @Test
    public void testMakeTransaction_Success() throws SuspendedAccountException {
        when(transactionService.makeTransaction(anyLong(), any(TransactionDto.class))).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.makeTransaction(1L, transactionDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transaction, response.getBody());

        verify(transactionService, times(1)).makeTransaction(1L, transactionDto);
    }

    @Test
    public void testMakeTransaction_SuspendedAccountException() throws SuspendedAccountException {
        when(transactionService.makeTransaction(anyLong(), any(TransactionDto.class))).thenThrow(new SuspendedAccountException("Account is suspended."));

        ResponseEntity<Transaction> response = transactionController.makeTransaction(1L, transactionDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // For now, not handled explicitly in controller

        verify(transactionService, times(1)).makeTransaction(1L, transactionDto);
    }

    @Test
    public void testGetTransactionHistory() {
        List<Transaction> transactionList = Arrays.asList(transaction);
        Page<Transaction> transactions = new PageImpl<>(transactionList);

        when(transactionService.getTransactionHistory(1L, 0, 10)).thenReturn(transactions);

        ResponseEntity<Page<Transaction>> response = transactionController.getTransactionHistory(1L, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());

        verify(transactionService, times(1)).getTransactionHistory(1L, 0, 10);
    }
}

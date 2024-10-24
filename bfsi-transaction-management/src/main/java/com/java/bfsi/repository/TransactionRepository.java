package com.java.bfsi.repository;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.java.bfsi.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccount_Id(Long accountId, Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.id = :accountId AND FUNCTION('DATE', t.timestamp) = :date AND t.type = 'dr'")
    BigDecimal sumWithdrawalsToday(@Param("accountId") Long accountId, @Param("date") LocalDate date);
}
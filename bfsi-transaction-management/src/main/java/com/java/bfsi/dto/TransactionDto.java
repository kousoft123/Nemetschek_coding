package com.java.bfsi.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class TransactionDto {
	private BigDecimal amount;
    private String type; // 'credit' or 'debit'
}
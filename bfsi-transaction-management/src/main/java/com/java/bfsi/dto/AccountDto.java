package com.java.bfsi.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class AccountDto {
	 private String accountno;
	 private BigDecimal initialBalance;
}
package com.java.bfsi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.bfsi.model.Account;

import jakarta.persistence.criteria.Predicate;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
	

}

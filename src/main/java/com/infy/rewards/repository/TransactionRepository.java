package com.infy.rewards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.rewards.entity.Transaction;

/**
 * Repository interface for {@link Transaction} entities.
 * 
 *  *
 * @author jaypalsingh
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>  {
	
}

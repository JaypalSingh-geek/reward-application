package com.infy.rewards.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transaction Entity Class.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
	private Customer customer;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "transaction_date", nullable = false)
	private LocalDateTime transactionDate;

	// Constructor for initializing fields
	public Transaction(Customer customer, BigDecimal amount, LocalDateTime transactionDate) {
		this.customer = customer;
		this.amount = amount;
		this.transactionDate = transactionDate;
	}
}

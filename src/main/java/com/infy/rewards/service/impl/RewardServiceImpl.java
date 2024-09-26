package com.infy.rewards.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.rewards.entity.Transaction;
import com.infy.rewards.exception.RewardNotFoundException;
import com.infy.rewards.repository.TransactionRepository;
import com.infy.rewards.response.model.CustomerReward;
import com.infy.rewards.service.RewardServiceIf;

import lombok.extern.slf4j.Slf4j;

/**
 * The implementation class to retrieve calculated Rewards Response.
 * 
 * This class handles the calculation of reward points based on customer
 * transactions. It retrieves transactions from the repository and processes
 * them to calculate rewards.
 * 
 */
@Slf4j
@Service
public class RewardServiceImpl implements RewardServiceIf {

	@Autowired
	private TransactionRepository transactionRepository;

	/**
	 * Calculates rewards for all customers based on their transactions.
	 *
	 * @return A map where the key is the customer ID and the value is their
	 *         respective CustomerReward.
	 * @throws RewardNotFoundException If fetching transactions fails.
	 */
	@Transactional
	public Map<String, CustomerReward> calculateRewards() {
		log.info("Calculating rewards for all customers.");
		List<Transaction> transactions;

		try {
			transactions = transactionRepository.findAll();
			log.info("Number of transactions fetched: {}", transactions != null ? transactions.size() : "null");
		} catch (Exception e) {
			log.error("Error occurred while fetching transactions: {}", e.getMessage());
			throw new RewardNotFoundException("Failed to fetch transactions", "REWARD_NOT_FOUND", e);
		}

		// Handle the case where transactions are null or empty
		if (transactions == null || transactions.isEmpty()) {
			log.warn("No transactions found.");
			return Collections.emptyMap(); // Return an empty map if no transactions exist
		}

		return transactions.stream().filter(this::isValidTransaction)
				.collect(Collectors.toMap(transaction -> transaction.getCustomer().getCustomerId(),
						this::createCustomerReward, this::aggregateRewards));
	}

	/**
	 * Validates a transaction to ensure it has a customer and a valid amount.
	 *
	 * @param transaction The transaction to validate.
	 * @return true if the transaction is valid; false otherwise.
	 */
	private boolean isValidTransaction(Transaction transaction) {
		boolean isValid = Optional.ofNullable(transaction).map(t -> t.getCustomer() != null && t.getAmount() != null)
				.orElse(false);
		log.info("Transaction valid: {} for transaction: {}", isValid, transaction);
		return isValid;
	}

	/**
	 * Creates a CustomerReward object based on the given transaction.
	 *
	 * @param transaction The transaction to process.
	 * @return A CustomerReward object for the customer associated with the
	 *         transaction.
	 */
	private CustomerReward createCustomerReward(Transaction transaction) {
		String customerId = transaction.getCustomer().getCustomerId();
		int points = calculateRewardPoints(transaction.getAmount());
		log.info("Calculating points for {}: {}", customerId, points);

		CustomerReward reward = new CustomerReward();
		reward.setCustomerId(customerId);
		reward.setMonthlyPoints(points);
		reward.setTotalPoints(points);
		return reward;
	}

	/**
	 * Aggregates rewards from multiple transactions for a single customer.
	 *
	 * @param existing  The existing CustomerReward to update.
	 * @param newReward The new CustomerReward to aggregate.
	 * @return The updated CustomerReward with aggregated points.
	 */
	private CustomerReward aggregateRewards(CustomerReward existing, CustomerReward newReward) {
		existing.setMonthlyPoints(existing.getMonthlyPoints() + newReward.getMonthlyPoints());
		existing.setTotalPoints(existing.getTotalPoints() + newReward.getTotalPoints());
		log.info("Aggregating rewards for {}: {}", existing.getCustomerId(), existing);
		return existing;
	}

	/**
	 * Calculates reward points based on the transaction amount.
	 *
	 * @param amount The amount of the transaction.
	 * @return The calculated points based on the amount.
	 */
	public int calculateRewardPoints(BigDecimal amount) {
		return Optional.ofNullable(amount).map(a -> {
			int points = 0;
			if (a.compareTo(new BigDecimal("100")) > 0) {
				points += (a.subtract(new BigDecimal("100"))).intValue() * 2;
				points += 50;
			} else if (a.compareTo(new BigDecimal("50")) > 0) {
				points += (a.subtract(new BigDecimal("50"))).intValue();
			}
			return points;
		}).orElse(0);
	}
}

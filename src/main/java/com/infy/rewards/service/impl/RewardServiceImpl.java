package com.infy.rewards.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
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
 * @author jaypalsingh
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
			log.info("Number of transactions fetched: {}", transactions.size());
		} catch (Exception e) {
			log.error("Error occurred while fetching transactions: {}", e.getMessage());
			throw new RewardNotFoundException("Failed to fetch transactions", "REWARD_NOT_FOUND", e);
		}

		if (transactions == null || transactions.isEmpty()) {
			log.warn("No transactions found.");
			return new HashMap<>(); // Return an empty map if no transactions exist
		}

		return transactions.stream().filter(transaction -> {
			boolean isValid = Optional.ofNullable(transaction)
					.map(t -> t.getCustomer() != null && t.getAmount() != null).orElse(false);
			log.info("Transaction valid: {} for transaction: {}", isValid, transaction);
			return isValid;
		}).collect(Collectors.toMap(transaction -> transaction.getCustomer().getCustomerId(), transaction -> {
			int points = calculateRewardPoints(transaction.getAmount());
			log.info("Calculating points for {}: {}", transaction.getCustomer().getCustomerId(), points);
			CustomerReward reward = new CustomerReward();
			reward.setCustomerId(transaction.getCustomer().getCustomerId());
			reward.setMonthlyPoints(points);
			reward.setTotalPoints(points);
			return reward;
		}, (existing, newReward) -> {
			existing.setMonthlyPoints(existing.getMonthlyPoints() + newReward.getMonthlyPoints());
			existing.setTotalPoints(existing.getTotalPoints() + newReward.getTotalPoints());
			log.info("Aggregating rewards for {}: {}", existing.getCustomerId(), existing);
			return existing;
		}));
	}

	/**
	 * Calculates reward points based on the transaction amount.
	 *
	 * @param amount The amount of the transaction.
	 * @return The calculated points based on the amount.
	 */
	public int calculateRewardPoints(BigDecimal amount) {
		if (amount == null)
			return 0;

		int points = 0;
		if (amount.compareTo(new BigDecimal("100")) > 0) {
			points += (amount.subtract(new BigDecimal("100"))).intValue() * 2;
			points += 50; // Flat 50 points for the first $100
		} else if (amount.compareTo(new BigDecimal("50")) > 0) {
			points += (amount.subtract(new BigDecimal("50"))).intValue();
		}
		return points;
	}
}

package com.infy.rewards.service.impl.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infy.rewards.entity.Customer;
import com.infy.rewards.entity.Transaction;
import com.infy.rewards.exception.RewardNotFoundException;
import com.infy.rewards.repository.TransactionRepository;
import com.infy.rewards.response.model.CustomerReward;
import com.infy.rewards.service.impl.RewardServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

	@InjectMocks
	private RewardServiceImpl rewardService;

	@Mock
	private TransactionRepository transactionRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCalculateRewards_SuccessfulCalculation() {
		Customer customer1 = new Customer("CUST002", "John Doe", "john@example.com");
		Customer customer2 = new Customer("CUST001", "Jane Doe", "jane@example.com");

		Transaction transaction1 = new Transaction(customer1, new BigDecimal("150"), LocalDateTime.now());
		Transaction transaction2 = new Transaction(customer2, new BigDecimal("50"), LocalDateTime.now());
		List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

		when(transactionRepository.findAll()).thenReturn(transactions);
		Map<String, CustomerReward> rewards = rewardService.calculateRewards();

		assertEquals(2, rewards.size());
		assertReward(rewards.get("CUST002"), 150, 150);
		assertReward(rewards.get("CUST001"), 0, 0);
	}

	@Test
	void testCalculateRewards_NoTransactions() {
		when(transactionRepository.findAll()).thenReturn(Collections.emptyList());
		Map<String, CustomerReward> rewards = rewardService.calculateRewards();
		assertTrue(rewards.isEmpty());
	}

	@Test
	void testCalculateRewards_NullTransactions() {
		when(transactionRepository.findAll()).thenReturn(null);
		Map<String, CustomerReward> rewards = rewardService.calculateRewards();
		assertTrue(rewards.isEmpty());
	}

	@Test
	void testCalculateRewards_InvalidTransaction() {
		Transaction transactionWithNullCustomer = new Transaction(null, new BigDecimal("100"), LocalDateTime.now());
		Transaction transactionWithNullAmount = new Transaction(new Customer("CUST001", "John Doe", "john@example.com"),
				null, LocalDateTime.now());
		List<Transaction> transactions = Arrays.asList(transactionWithNullCustomer, transactionWithNullAmount);

		when(transactionRepository.findAll()).thenReturn(transactions);
		Map<String, CustomerReward> rewards = rewardService.calculateRewards();

		assertTrue(rewards.isEmpty());
	}

	@Test
	void testCalculateRewards_ExceptionDuringFetch() {
		when(transactionRepository.findAll()).thenThrow(new RuntimeException("Database error"));
		RewardNotFoundException exception = assertThrows(RewardNotFoundException.class,
				() -> rewardService.calculateRewards());
		assertEquals("Failed to fetch transactions", exception.getMessage());
		assertEquals("REWARD_NOT_FOUND", exception.getErrorCode());
	}

	@Test
	void testCalculateRewards_MultipleTransactionsForSameCustomer() {
		Customer customer = new Customer("CUST001", "John Doe", "john@example.com");
		Transaction transaction1 = new Transaction(customer, new BigDecimal("100"), LocalDateTime.now());
		Transaction transaction2 = new Transaction(customer, new BigDecimal("150"), LocalDateTime.now());
		List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

		when(transactionRepository.findAll()).thenReturn(transactions);
		Map<String, CustomerReward> rewards = rewardService.calculateRewards();

		assertEquals(1, rewards.size());
		CustomerReward reward = rewards.get("CUST001");
		assertEquals(200, reward.getTotalPoints());
		assertEquals(200, reward.getMonthlyPoints());
	}

	@Test
	@DisplayName("Test calculatePoints for various purchase amounts")
	void testCalculatePoints() {
		assertRewardPoints(new BigDecimal("120"), 90);
		assertRewardPoints(new BigDecimal("100"), 50);
		assertRewardPoints(new BigDecimal("70"), 20);
		assertRewardPoints(new BigDecimal("40"), 0);
	}

	@Test
	void testCalculateRewardPoints_NullAmount() {
		assertEquals(0, rewardService.calculateRewardPoints(null));
	}

	private void assertReward(CustomerReward reward, int expectedMonthlyPoints, int expectedTotalPoints) {
		assertEquals(expectedMonthlyPoints, reward.getMonthlyPoints());
		assertEquals(expectedTotalPoints, reward.getTotalPoints());
	}

	private void assertRewardPoints(BigDecimal amount, int expectedPoints) {
		assertEquals(expectedPoints, rewardService.calculateRewardPoints(amount));
	}
}

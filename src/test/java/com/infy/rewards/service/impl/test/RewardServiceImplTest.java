package com.infy.rewards.service.impl.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
	void testCreateCustomerReward() {
		// Set up a mock transaction
		Customer customer = new Customer("CUST001", "John Doe", "john.doe@example.com");
		Transaction transaction = new Transaction(customer, new BigDecimal("120"), LocalDateTime.now());

		// Invoke createCustomerReward method
		CustomerReward reward = rewardService.createCustomerReward(transaction);

		// Validate the created CustomerReward object
		assertEquals("CUST001", reward.getCustomerId());
		assertEquals("John Doe", reward.getName());
		assertEquals("john.doe@example.com", reward.getEmail());
		assertEquals(90, reward.getTotalPoints()); // Assuming 90 points for $120
		Map<String, Integer> expectedMonthlyPoints = new HashMap<>();
		expectedMonthlyPoints.put(transaction.getTransactionDate().getMonth().toString(), 90);
		assertEquals(expectedMonthlyPoints, reward.getMonthlyPoints());
	}

	@Test
	void testAggregateRewards() {
		CustomerReward existingReward = new CustomerReward();
		existingReward.setTotalPoints(100);
		Map<String, Integer> existingMonthlyPoints = new HashMap<>();
		existingMonthlyPoints.put("OCTOBER", 50);
		existingReward.setMonthlyPoints(existingMonthlyPoints);

		CustomerReward newReward = new CustomerReward();
		newReward.setTotalPoints(150);
		Map<String, Integer> newMonthlyPoints = new HashMap<>();
		newMonthlyPoints.put("OCTOBER", 100);
		newReward.setMonthlyPoints(newMonthlyPoints);
		CustomerReward aggregatedReward = rewardService.aggregateRewards(existingReward, newReward);

		// Validate the aggregated results
		assertEquals(250, aggregatedReward.getTotalPoints()); // 100 + 150
		assertEquals(150, aggregatedReward.getMonthlyPoints().get("OCTOBER")); // 50 + 100
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

	@Test
	private void assertReward(CustomerReward reward, String expectedName, String expectedEmail,
			Map<String, Integer> expectedMonthlyPoints, int expectedTotalPoints) {
		assertEquals(expectedName, reward.getName());
		assertEquals(expectedEmail, reward.getEmail());
		assertEquals(expectedMonthlyPoints, reward.getMonthlyPoints());
		assertEquals(expectedTotalPoints, reward.getTotalPoints());
	}

	@Test
	private void assertRewardPoints(BigDecimal amount, int expectedPoints) {
		assertEquals(expectedPoints, rewardService.calculateRewardPoints(amount));
	}
}

package com.infy.rewards.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infy.rewards.exception.RewardNotFoundException;
import com.infy.rewards.repository.TransactionRepository;
import com.infy.rewards.response.model.CustomerReward;
import com.infy.rewards.service.impl.RewardServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private RewardServiceImpl rewardService; // Adjust this to your service class name

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testCalculateRewardsWithEmptyTransactions() {
		when(transactionRepository.findAll()).thenReturn(Arrays.asList());
		Map<String, CustomerReward> rewardsMap = rewardService.calculateRewards();
		assertTrue(rewardsMap.isEmpty());
	}

	@Test
	void testCalculateRewards_Failure() {
		when(transactionRepository.findAll()).thenThrow(new RuntimeException("Database error"));

		RewardNotFoundException exception = assertThrows(RewardNotFoundException.class, () -> {
			rewardService.calculateRewards();
		});

		assertEquals("Failed to fetch transactions", exception.getMessage());
		assertEquals("REWARD_NOT_FOUND", exception.getErrorCode());
	}

	@Test
	@DisplayName("Test calculatePoints for various purchase amounts")
	void testCalculatePoints() {
		// when purchase > 100
		assertEquals(90, rewardService.calculateRewardPoints(new BigDecimal("120")));
		// when purchase = 100
		assertEquals(50, rewardService.calculateRewardPoints(new BigDecimal("100")));
		// when purchase >50 but <100
		assertEquals(20, rewardService.calculateRewardPoints(new BigDecimal("70")));
		// when purchase <= 50
		assertEquals(0, rewardService.calculateRewardPoints(new BigDecimal("40")));
	}

}

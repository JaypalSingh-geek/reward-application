package com.infy.rewards.service;

import java.util.Map;

import com.infy.rewards.response.model.CustomerReward;

/**
 * Interface for calculating customer rewards.
 * Implementations should return a map of customer IDs to their rewards.
 * 
 * @author jaypalsingh
 * @version 1.0
 * @since 2024-09-26
 */
public interface RewardServiceIf {

	/**
	 * Calculates and returns customer rewards.
	 *
	 * @return a map with customer IDs as keys and their corresponding
	 *         {@link CustomerReward} objects as values.
	 */
	Map<String, CustomerReward> calculateRewards();

}

package com.infy.rewards.response.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 * The customer reward class.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReward {

	private String customerId;
	private int monthlyPoints;
	private int totalPoints;

}
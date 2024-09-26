package com.infy.rewards.response.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.Map;

/**
 * The customer reward class.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReward {
	private String customerId;
	private String name; // Add customer name
	private String email; // Add customer email
	private Map<String, Integer> monthlyPoints; // Change to Map for monthly points
	private int totalPoints;
}

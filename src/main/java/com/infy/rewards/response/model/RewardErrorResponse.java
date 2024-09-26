package com.infy.rewards.response.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Reward ErrorResponse Class.
 */
@AllArgsConstructor
@Data
public class RewardErrorResponse {

	private String errorCode;
	private String message;
	private Object result;

}

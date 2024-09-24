package com.infy.rewards.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.rewards.response.model.CustomerReward;
import com.infy.rewards.service.RewardServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


/**
 * The Reward Controller.
 * 
 * @author JaypalSingh
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Rewards Controller", description = "Operations related to customer rewards")
public class RewardController {

	@Autowired
	private RewardServiceImpl rewardService;

	
	@Operation(summary = "Get customer rewards", 
               description = "Calculates and retrieves the reward points for customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved rewards"),
        @ApiResponse(responseCode = "400", description = "Bad request due to invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
	@GetMapping("/rewards")
	public ResponseEntity<Map<String, CustomerReward>> getRewards() {
		log.info("Received request to calculate rewards.");
		Map<String, CustomerReward> rewards = rewardService.calculateRewards();
		return ResponseEntity.ok(rewards);
	}

}

package com.infy.rewards.controller.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.infy.rewards.exception.RewardNotFoundException;
import com.infy.rewards.response.model.CustomerReward;
import com.infy.rewards.service.impl.RewardServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RewardServiceImpl rewardService;

	@Test
	public void testGetRewards_Positive() throws Exception {
		Map<String, CustomerReward> rewardsMap = new HashMap<>();
		when(rewardService.calculateRewards()).thenReturn(rewardsMap);
		mockMvc.perform(get("/api/rewards")).andExpect(status().isOk());
	}

	@Test
	public void testGetRewards_NoTransactions() throws Exception {
		when(rewardService.calculateRewards())
				.thenThrow(new RewardNotFoundException("No rewards found", "REWARD_NOT_FOUND", null));

		mockMvc.perform(get("/api/rewards")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorCode").value("REWARD_NOT_FOUND"));
	}
}

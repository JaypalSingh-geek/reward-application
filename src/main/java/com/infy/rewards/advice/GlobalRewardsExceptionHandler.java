package com.infy.rewards.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.infy.rewards.exception.RewardNotFoundException;
import com.infy.rewards.response.model.RewardErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalRewardsExceptionHandler {

	/**
	 * Handles RewardNotFoundException and returns a structured error response.
	 *
	 * @param e the RewardNotFoundException thrown
	 * @return ResponseEntity containing the error details
	 */
	@Operation(summary = "Handle Reward Not Found Exception", 
			description = "Returns a 404 Not Found response with error details when a reward is not found.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Reward not found", 
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = RewardErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@ExceptionHandler(RewardNotFoundException.class)
	public ResponseEntity<RewardErrorResponse> handleRewardNotFound(RewardNotFoundException e) {
		log.warn("Reward not found: {}", e.getMessage());

		RewardErrorResponse errorResponse = new RewardErrorResponse(e.getErrorCode(), e.getMessage(), e.getResult());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	/**
	 * Handles general exceptions and returns a generic error message.
	 *
	 * @param e the Exception thrown
	 * @return ResponseEntity with a generic error message
	 */
	@Operation(summary = "Handle General Exceptions", 
			description = "Returns a 500 Internal Server Error response for unexpected errors.")
	@ApiResponses(value = { @ApiResponse(responseCode = "500", description = "Internal server error") })
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception e) {
		log.error("An unexpected error occurred: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("An unexpected error occurred: " + e.getMessage());
	}

	/**
	 * Handles IllegalArgumentException and returns a bad request response.
	 *
	 * @param ex the IllegalArgumentException thrown
	 * @return ResponseEntity containing the error message
	 */
	@Operation(summary = "Handle Illegal Argument Exception", 
			description = "Returns a 400 Bad Request response with error details for invalid arguments.")
	@ApiResponses(value = { @ApiResponse(responseCode = "400", description = "Bad request due to invalid argument") })
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
		log.error("Invalid argument: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}

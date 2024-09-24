package com.infy.rewards.exception;


/**
 * The Class RewardNotFoundException.
 * 
 * @author jaypalsingh
 */
public class RewardNotFoundException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;
    private final String errorCode;
    private final Object result; // Additional information or result from the method

    
    /**
     * Constructs a new RewardNotFoundException with the specified detail message,
     * error code, and additional result information.
     *
     * @param message the detail message
     * @param errorCode the specific error code
     * @param result additional information related to the error
     */
    public RewardNotFoundException(String message, String errorCode, Object result) {
        super(message);
        this.errorCode = errorCode;
        this.result = result;
    }

    
    /**
     * Constructs a new RewardNotFoundException with the specified detail message,
     * cause, error code, and additional result information.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param errorCode the specific error code
     * @param result additional information related to the error
     */
    public RewardNotFoundException(String message, Throwable cause, String errorCode, Object result) {
        super(message, cause);
        this.errorCode = errorCode;
        this.result = result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getResult() {
        return result;
    }
}

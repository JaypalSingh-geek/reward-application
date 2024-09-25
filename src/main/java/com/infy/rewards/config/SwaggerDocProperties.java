package com.infy.rewards.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * This class defines the SwaggerDoc Properties.
 * 
 * @author Reference Architecture
 *
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "swagger-docs")
public class SwaggerDocProperties {

	/** The docs. */
	@NestedConfigurationProperty
	private SwaggerDocs docs = new SwaggerDocs();

	@Getter
	@Setter
	public static class SwaggerDocs {
		private String basePath = "/";
		private String title;
		private String description;
		private String version;
		private String contact;
	}

}

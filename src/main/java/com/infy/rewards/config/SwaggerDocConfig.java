package com.infy.rewards.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.AllArgsConstructor;


/**
 * This class defines the SwaggerDoc Config Feature.
 * 
 * @author Jaypal Singh
 *
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(SwaggerDocProperties.class)
public class SwaggerDocConfig {

	private final SwaggerDocProperties properties;
	
    /**
     * Customizes the OpenAPI documentation.
     *
     * @return An OpenAPI instance containing metadata for the API.
     */
	@Bean
	OpenAPI customOpenAPI() {
		SwaggerDocProperties.SwaggerDocs docs = properties.getDocs();
		return new OpenAPI()
				.info(new Info().title(docs.getTitle()).description(docs.getDescription()).version(docs.getVersion())
						.contact(new io.swagger.v3.oas.models.info.Contact().name(docs.getContact())));
	}
}

package org.ultimateam.apiultimate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.ultimateam.apiultimate.configuration.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ApiUltimateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiUltimateApplication.class, args);
	}
}

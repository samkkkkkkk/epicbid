package com.example.epicbid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EpicbidApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpicbidApplication.class, args);
	}

}

package com.contable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ContableApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContableApplication.class, args);
	}

}

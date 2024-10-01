package com.smsc.management;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
@EnableAsync
public class SmscManagementBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmscManagementBeApplication.class, args);
	}
}

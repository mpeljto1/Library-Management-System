package com.example.lmswebappui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@EnableDiscoveryClient
//@EnableFeignClients("com.example.lmswebappui")
public class LmsWebAppUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsWebAppUiApplication.class, args);
	}
}

package com.etransact.ussd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
@ServletComponentScan
@SpringBootApplication
public class UssdReceiverApplication {

	public static void main(String[] args) {      
		SpringApplication.run(UssdReceiverApplication.class, args);
	}

}

package com.etransact.ussd.ussdReceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
@ServletComponentScan
@SpringBootApplication
public class UssdReceiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(UssdReceiverApplication.class, args);
	}

}

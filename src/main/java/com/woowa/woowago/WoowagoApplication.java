package com.woowa.woowago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WoowagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoowagoApplication.class, args);
	}

}

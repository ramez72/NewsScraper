package com.ramesh.news;


import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
public class NewsScraperApplication {
	
	Logger logger= Logger.getLogger(this.getClass().getName());

	public static void main(String[] args) {
		SpringApplication.run(NewsScraperApplication.class, args);
	}

}

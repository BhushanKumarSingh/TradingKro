package com.trading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class Schedular {
	@Autowired
	NiftyOIService niftyOIService;
	
	@Scheduled(fixedDelay = 5  * 60 * 1000)
	public void fetchData() {
		try {
			niftyOIService.oiData();
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}

package com.trading.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.service.NiftyOIService;

@RestController
@RequestMapping("trading")
public class NiftyOIController {

	@Autowired
	NiftyOIService niftyOIService;

	@GetMapping("/oi")
	public String getOi() {
    return "Hello Bhushan";
	}

}

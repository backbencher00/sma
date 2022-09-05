package com.sma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sma.controller.response.SmaApiResponse;
import com.sma.service.SmaService;

@RestController
public class SmaController {
	
	@Autowired
	SmaService service;
	
	@GetMapping("/fare")
	public SmaApiResponse getFare(@RequestParam("file") MultipartFile file) throws Exception {
		return service.calculateFare(file);
		
	}
}

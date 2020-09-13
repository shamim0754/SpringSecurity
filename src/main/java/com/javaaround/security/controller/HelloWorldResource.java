package com.javaaround.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldResource {

	@RequestMapping("/hello")
	public String geHelloWorld(){
		return "Hello world ";
	}

	@RequestMapping("/admin")
	public String geAdminHelloWorld(){
		return "Admin Hello world ";
	}
}

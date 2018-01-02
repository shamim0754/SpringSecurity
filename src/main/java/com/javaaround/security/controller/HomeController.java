package com.javaaround.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	
	@GetMapping("/")
	String index(@RequestParam(value="name", required=false, defaultValue="shamim") String name, Model model) {
        model.addAttribute("name", name);
		return "index";
	}
}

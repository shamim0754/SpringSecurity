### Spring Security ###
1. It secure both java SE/EE app
2. Provides powerful and elegant security mechanism
3. Provides Declarative Security Programming(No need own logic code)
4. Easy of Extendability

### Spring Security on Spring Boot App ###

Create App Boot App using spring boot cli

`spring init -n=SpringSecurity -g=com.javaaround --package-name=com.javaaround.security -d=Web,Security,JPA,Thymeleaf,MySql,DevTools --build=maven SpringSecurity`

Create Home Controller.java

```java
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

```

Create index.html

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Getting Started: Serving Web Content</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
    <p th:text="'Hello, ' + ${name} + '!'" />
</body>
</html>
```

Thats it . Your application is secure without writing any code

browse your app

`http://localhost:8080/`

it needs username / password to access content

By default spring provide 

`username : user` <br>
`password: show at console`

By above credential you can access your app




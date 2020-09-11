### Spring Security ###
1. It secure both java SE/EE app. it provides both authentication and authorization
    1. authentication : check who want to enter into system. it checks username/password valid
    2. authorization : which part of app he access it controls that
2. Provides powerful and elegant security mechanism
3. Provides Declarative Security Programming(No need own logic code)
4. Easy of Extendability

### Architecture ###
![](images/spring-security-architecture.png)

Spring security combinations of pre and post servlet filter called `filterchain`. DelegatingFilterProxy just delegates request to `filterchain`. filterchain use two thing to process filter
1. Authenticate Manager : it delegates request to `UserDetailsService` thats loads `UserDetails`
2. Security Context : it contains `UserDetails` after authentication
 Note : spring implemented class (`User`) .it use `GrandtedAuthorities` . 
 spring implemented class (`Authorities`) object
### Spring Security on Spring Boot App ###

Create App Boot App using spring initialize
Note : select only web,security dependency

Create Home Controller.java

```java
package com.javaaround.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloWorldResource {

    @RequestMapping("/")
    public String geHelloWorld(){
        return "Hello world ";
    }
}

```


Thats it . Your application is secure without writing any code!

browse your app

`http://localhost:8080/`

it needs username / password to access content

By default spring provide single user with following credential

`username : user` <br>
`password: show at console`

By above credential you can access your app

We can override above default credential by setting following property
`spring.security.user.name=admin
 spring.security.user.password=124`
 
Rerun app<br/>
then we can access page by our credential 

## Setting multiple user ##
We know any application does not only single user. we  have multiple user to access app .User can have many place
1. In memory
2. at database
3. at LDAP Server
4. at Oauth environment

## In memory authentication ###
Create a SecurityConfiguration.java <br />

```
@Configuration
@EnableWebSecurity
public class SecurityConfiguration  extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("user").authorities("ROLE_USER").and()
                .withUser("admin").password("admin").authorities("ROLE_ADMIN");
    }

}

```

1. `@EnableWebSecurity` is marker annotation is used to mark the spring mvc to use our security configuration instead of build in security configuration. if this annotation
is missing then it will use built in security configuration
2. class must extends `WebSecurityConfigurerAdapter` . Since we need Authentication(In memory) of our application . so we need override following method
`protected void configure(AuthenticationManagerBuilder auth) throws Exception`
3. Then we add two user with authority(discuss later)

Rerun app
Try login but can't success. we see following line at console

`java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"`

That means password must be hash at current spring security but our case it is plain text <br />

add a encode and encode your password by following Setting

```
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder())
                .withUser("user").password(passwordEncoder().encode("user")).authorities("ROLE_USER").and()
                .withUser("admin").password(passwordEncoder().encode("admin")).authorities("ROLE_ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
```

Note : if you still need plain text password you can use
`.passwordEncoder(NoOpPasswordEncoder.getInstance())` or `.withUser("user").password("{noop}user").authorities("ROLE_USER")`


https://data-flair.training/blogs/spring-security-tutorial/
https://spring.io/guides/topicals/spring-security-architecture/
https://howtodoinjava.com/spring-security/jdbc-user-service-based-spring-security-example/
https://blog.codecentric.de/en/2017/08/localization-spring-security-error-messages-spring-boot/
https://www.youtube.com/watch?v=Q3yStECBuAg




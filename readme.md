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

Create a Boot App using  [spring initialize](https://start.spring.io/) 
<br />
Note : select only web,security dependency <br /> 
Note : here used 2.3.3.RELEASE

Create  HelloWorldResource.java

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


Thats it . Your application is secure without writing any code!!!!

browse your app

`http://localhost:8080/`

it needs username / password to access content

By default spring provide single user with following credential

`username : user` <br>
`password: show at console`

By above credential you can access your app

We can override above single user credential by setting following property <br /> 
`spring.security.user.name=admin
 spring.security.user.password=124`
 
Rerun app<br/>
then we can access page by our credential 

Note : spring provide by default `form based(showing that above) and basic authentication(later)`

## Setting multiple user ##
We know any application does not only single user. we  have multiple user to access app .Multiple User can have following place
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
```protected void configure(AuthenticationManagerBuilder auth) throws Exception```
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

Note : if you still need plain text password(not recommended on real app) you can use <br />
`.passwordEncoder(NoOpPasswordEncoder.getInstance())` 
or <br /> `.password("{noop}user")`


### Rest based app authentication ##
traditionally people use `mvc` that above way discuss so far(ui and server code at same app) but Nowadays people use separate ui from backend server. that means one app for UI and another app for backend server( called REST app) <br />
How can we authenticate of rest based app ? 

spring provides by default basic authentication  for that 

I am using popular api client tool postman

put url `localhost:8181/hello`  and send button click
 
 ```
{
    "timestamp": "2020-09-13T09:34:12.747+00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Unauthorized",
    "path": "/hello"
}
```

We may wonder same url when i put it browser it will give status code 302(redirect) and in case postman it says 401.<br />
The answer is request header `accept` . in case browser, browser will  send accept header value =`text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9`
then server detects it's a browser based app and send it status 302 with response header `location:http://localhost:8181/login` and based on location header url browser redirect on it 

<br />

In case postman,postman will  send accept header value = `*/*` then server detects client can handle any kind of response eq. json,xml etc. 
it case here server send json response

in postman, authorization option use auth type basic auth and give username and password and send button click again <br /> 

`Hello world `

it also send a cookie `JSESSIONID EF17B90BED8FE2A7D58E01476C0012CE` (you can see it at postman cookies tab).for subsequent request(instead of username and password),we can send above cookie
,then spring security take it is already authenticate user

Note : remaining demo only for rest api

### Authorization(Access control) ###
We know every user have one or more authority. based on user authority we authorize by following way
1. Url based authorization
2. Method level authorization

Url based authorization : For Url based authentication, two case there.

1. Url access without authenticate user(`permitAll()`)
2. Url access with authenticate user if have authority

For Url based authentication, we need to following method override on SecurityConfiguration.java

```
@Override
protected void configure(HttpSecurity http) throws Exception {
}
```

Rerun app <br />
now all url are open to all (no need authentication)!! because spring use our configuration but we don't provide configuration. <br />
We now add following configuration going to default state

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
            .formLogin()
                .and()
            .httpBasic();
}
```
Rerun app <br />
now it is working as expected

Since we are care about rest api. we remove form login

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
            .httpBasic();
}
```

Note : authenticated()  vs fullyAuthenticated().    authenticated() Returns true if the user is not anonymous
where fullyAuthenticated() Returns true if the user is not an anonymous or a remember-me user

Add a endpoint at  HelloWorldResource.java

```
    @RequestMapping("/admin")
	public String geAdminHelloWorld(){
		return "Admin Hello world ";
	}
}
```

Add authorization based on url ant matcher

```
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/hello").permitAll()
                    .antMatchers("/admin").hasRole("ADMIN")    
                    .anyRequest().fullyAuthenticated()
                    .and()
                .formLogin()
                    .and()
                .httpBasic();
    }
```

Note : `ant` keyword comes from apache ant directory pattern . you can apache  ant directory pattern[Link](https://ant.apache.org/manual/dirtasks.html)

Here `/hello` access no need authentication<br />
Here `/admin` access  authentication and authority 'ADMIN'

At postman <br />
`/hello` access url  no need authentication
`/admin` admin user access but basic auth provide user , he can't access

https://data-flair.training/blogs/spring-security-tutorial/
https://spring.io/guides/topicals/spring-security-architecture/
https://howtodoinjava.com/spring-security/jdbc-user-service-based-spring-security-example/
https://blog.codecentric.de/en/2017/08/localization-spring-security-error-messages-spring-boot/
https://www.youtube.com/watch?v=Q3yStECBuAg




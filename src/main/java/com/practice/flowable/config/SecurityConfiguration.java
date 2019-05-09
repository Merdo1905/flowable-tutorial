package com.practice.flowable.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        //this.is.too.boring.but.i.was.too.lazy.to.implement.an.authentication.mechanism.so.i.authorize.all.requests
        http.
                //this little finger allow us to use api's from postman
                        csrf()
                .disable()
                //this little guy allow us to use api's without "a" User because even if there is no authentication a User must be present at default
                .anonymous()
                .and()
                //this little finger allow us to use H2 Console on local browsers
                .headers()
                .frameOptions()
                .sameOrigin()//This little
                //this little finger allow anywhere to use api's
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }

}

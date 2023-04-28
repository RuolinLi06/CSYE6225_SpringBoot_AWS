package com.neu.webapp.config;

import com.neu.webapp.service.inplement.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author Ruolin Li
 */
@Configuration
//@EnableWebSecurity
public class WebSecurityConfigure {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private CustomAuthenticationProvider customAuthenticationProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //http.authenticationProvider(customAuthenticationProvider);
        http.authorizeRequests()
                .requestMatchers("/healthz").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(HttpMethod.POST,"/v1/user")
                .requestMatchers(HttpMethod.GET,"/v1/product/{id}");
    }

        //basic auth with DaoAuthenticationProvider();
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

}
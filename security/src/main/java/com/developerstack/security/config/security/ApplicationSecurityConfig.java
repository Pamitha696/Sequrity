package com.developerstack.security.config.security;

import com.developerstack.security.jwt_config.JwtAuthenticationFilter;
import com.developerstack.security.jwt_config.JwtTokenVerifier;
import com.developerstack.security.service.ApplicationUserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.developerstack.security.config.permission.ApplicationUserRole.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
   private final PasswordEncoder passwordEncoder;

   private final ApplicationUserServiceImpl applicationUserService;

    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserServiceImpl applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http.
                    csrf().disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                    .addFilterAfter(new JwtTokenVerifier(),JwtAuthenticationFilter.class)
                    .authorizeRequests()
                            .antMatchers("/","index").permitAll()
                    .antMatchers("/api/v1/**").hasRole(USER.name())
                    .anyRequest()
                    .authenticated();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
          auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}

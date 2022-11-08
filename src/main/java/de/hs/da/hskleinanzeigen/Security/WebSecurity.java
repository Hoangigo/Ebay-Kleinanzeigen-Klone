package de.hs.da.hskleinanzeigen.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("user")
        .password("{noop}user") //don't encode password
        .roles("Benutzer")
        .and()
        .withUser("admin")
        .password("{noop}admin")
        .roles("Administrator")
    ;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .antMatchers("/actuator/health", "/actuator/info").permitAll()
        .antMatchers("/api/advertisements").hasAnyRole("Benutzer", "Administrator")
        .antMatchers("/actuator/*").hasRole("Administrator")
        .anyRequest().authenticated()
        .and()
        .httpBasic()
    ;
    //we have stopped the csrf to make post method work
    http.cors()
        .and()
        .csrf()
        .disable()
    ;
  }

}

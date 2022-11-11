package de.hs.da.hskleinanzeigen.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurity {

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails user = User.withUsername("user")
        .password(passwordEncoder().encode("user"))
        .roles("Benutzer")
        .build();
    UserDetails admin = User.withUsername("admin")
        .password(passwordEncoder().encode("admin"))
        .roles("Administrator")
        .build();
    return new InMemoryUserDetailsManager(admin, user);
  }


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .antMatchers("/actuator/health", "/actuator/info").permitAll()
        .antMatchers("/api/**").hasAnyRole("Benutzer", "Administrator")
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

    return http.build();
  }
}

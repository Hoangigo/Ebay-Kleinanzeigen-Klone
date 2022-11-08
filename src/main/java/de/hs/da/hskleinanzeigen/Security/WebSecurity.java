package de.hs.da.hskleinanzeigen.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class WebSecurity {

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails user = User.withUsername("user")
        .password("{noop}user") //don't encode password
        .roles("Benutzer")
        .build();
    UserDetails admin = User.withUsername("admin")
        .password("{noop}admin")
        .roles("Administrator")
        .build();
    return new InMemoryUserDetailsManager(admin, user);
  }


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .antMatchers("/actuator/health", "/actuator/info").permitAll()
        .antMatchers("/api/advertisements").hasAnyRole("Benutzer", "Administrator")
        .antMatchers("/actuator/*").hasRole("Administrator")
        .anyRequest().authenticated()
        .and()
        .httpBasic()
    ;
    return http.build();
  }
}

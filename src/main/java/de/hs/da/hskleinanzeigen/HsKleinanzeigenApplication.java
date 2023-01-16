package de.hs.da.hskleinanzeigen;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class HsKleinanzeigenApplication {

  public static void main(String[] args) {
    SpringApplication.run(HsKleinanzeigenApplication.class, args);
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI() //
        .info(new Info().title("HS Kleinanzeigen") //
            .description("Plattform zum Verwalten von Kleinanzeigen")
            .contact(new Contact().email("moise.j.chengo@stud.h-da.de"))
            .version("v2.0.22"));
  }
}

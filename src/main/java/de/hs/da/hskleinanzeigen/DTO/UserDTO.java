package de.hs.da.hskleinanzeigen.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDTO {

  @EqualsAndHashCode.Include
  private Integer id;

  @NotEmpty(message = "Eine Email Adresse müssen Sie eingeben")
  @Email(message = "Geben Sie eine gültige  Email Adresse ein!!")
  private String email;

  @NotEmpty(message = "Ein passwort fehlt noch")
  @Size(min = 6, message = "Passwort muss mindestens 6 Zeichen enthalten")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @NotEmpty(message = "Vorname fehlt noch")
  @Size(max = 255, message = "Vorname soll nicht länger als 255 Zeichen")
  private String firstName;

  @NotEmpty(message = "Nachname fehlt noch")
  @Size(max = 255, message = "Nachname soll nicht länger als 255 Zeichen")
  private String lastName;

  private String phone;

  private String location;
}

package de.hs.da.hskleinanzeigen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDTO implements Serializable { //TODO warum Serializable

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

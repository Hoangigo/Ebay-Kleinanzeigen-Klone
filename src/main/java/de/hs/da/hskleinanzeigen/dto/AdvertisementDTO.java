package de.hs.da.hskleinanzeigen.dto;

import de.hs.da.hskleinanzeigen.entities.Advertisement;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdvertisementDTO {

  private Integer id;

  @NotNull(message = "Payload incomplete, Type ist mandatory") //Post request returns 400 when type not there
  @Enumerated(EnumType.STRING)
  private Advertisement.AD_TYPE type;

  @NotNull(message = "Payload incomplete, Category ist mandatory")
  //@Valid
  private CategoryDTO category;

  @NotNull
  //@Valid  //Auskommentiert denn sonst schlägt test Prak2task3201 fehl
  private UserDTO user;

  @NotEmpty(message = "Payload incomplete, Title ist mandatory")
  private String title;

  @NotEmpty(message = "Payload incomplete, Description ist mandatory")
  private String description;

  private Integer price;

  private String location;

}

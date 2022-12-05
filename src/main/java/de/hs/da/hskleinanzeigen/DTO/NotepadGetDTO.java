package de.hs.da.hskleinanzeigen.DTO;

import javax.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotepadGetDTO {

  @EqualsAndHashCode.Include
  private Integer id;

  @Valid  //TODO hier nicht benötigt da nur zum Output, oder?
  private AdvertisementDTO advertisement;

  private String note;
}

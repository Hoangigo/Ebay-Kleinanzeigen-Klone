package de.hs.da.hskleinanzeigen.dto;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotepadGetDTO {

  @EqualsAndHashCode.Include
  private Integer id;

  @Valid  //hier nicht benötigt da nur zum Output, oder? //nicht wirklich, aber rein lassen
  private AdvertisementDTO advertisement;

  private String note;
}

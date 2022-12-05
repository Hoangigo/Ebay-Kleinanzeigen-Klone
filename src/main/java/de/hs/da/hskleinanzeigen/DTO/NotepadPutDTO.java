package de.hs.da.hskleinanzeigen.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotepadPutDTO {

  @EqualsAndHashCode.Include
  private Integer id;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY )
  private Integer userId;

  @NotNull(message = "Advertisement Id fehlt noch")
  private Integer advertisementId;

  private String note; //TODO Notepad ohne Note möglich? Note in DDL nullable, so gewollt?

}

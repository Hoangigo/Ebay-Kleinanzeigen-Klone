package de.hs.da.hskleinanzeigen.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoryDTO {

  @EqualsAndHashCode.Include
  private Integer id;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY )
  private Integer parentId;

  @NotEmpty(message = "you must give a name to the new category")
  private String name;
}

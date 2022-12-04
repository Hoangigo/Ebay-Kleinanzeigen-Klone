package de.hs.da.hskleinanzeigen.Mapper;

import de.hs.da.hskleinanzeigen.DTO.CategoryDTO;
import de.hs.da.hskleinanzeigen.Entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.WARN)
public interface CategoryMapper {

  @Mapping(target = "parent_id", source = "categoryDTO.parentId")
  Category toCategoryEntity(CategoryDTO categoryDTO);

  @Mapping(target = "parentId", source = "category.parent_id")
  CategoryDTO toCategoryDTO(Category category);
}

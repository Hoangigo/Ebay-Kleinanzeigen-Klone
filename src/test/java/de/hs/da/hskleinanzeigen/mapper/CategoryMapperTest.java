package de.hs.da.hskleinanzeigen.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.hs.da.hskleinanzeigen.dto.CategoryDTO;
import de.hs.da.hskleinanzeigen.entities.Category;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;


public class CategoryMapperTest {

  CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

  static Category generateCategory(String name) {
    Category cat = new Category();
    cat.setName(name);
    cat.setId(1);
    cat.setParent_id(2);
    return cat;
  }

  Category fromCategory;
  CategoryDTO fromCategoryDTO;

  @BeforeEach
  void setUp() {
    fromCategory = generateCategory("Corona");
    fromCategoryDTO = new CategoryDTO(11, 17, "erien5");
  }


  @Test
  void toCategoryDTO() {
    CategoryDTO dto = mapper.toCategoryDTO(fromCategory);
    assertThat(dto.getId()).isEqualTo(fromCategory.getId());
    assertThat(dto.getName()).isEqualTo(fromCategory.getName());
    assertThat(dto.getParentId()).isEqualTo(fromCategory.getParent_id());
  }

  @Test
  void toCategoryEntity() {
    Category category = mapper.toCategoryEntity(fromCategoryDTO);

    assertThat(category.getId()).isEqualTo(fromCategoryDTO.getId());
    assertThat(category.getName()).isEqualTo(fromCategoryDTO.getName());
    assertThat(category.getParent_id()).isEqualTo(fromCategoryDTO.getParentId());
  }
}

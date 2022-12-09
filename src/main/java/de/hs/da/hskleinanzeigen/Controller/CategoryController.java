package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.DTO.CategoryDTO;
import de.hs.da.hskleinanzeigen.Entities.Category;
import de.hs.da.hskleinanzeigen.Mapper.CategoryMapper;
import de.hs.da.hskleinanzeigen.Repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/categories")
@Tag(name = "Category", description = "Set new category and their properties")
public class CategoryController {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Autowired
  public CategoryController(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }


  @PostMapping(consumes = "application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @Operation(summary = "Create a new user")
  @ApiResponses({ //
      @ApiResponse(responseCode = "201", description = "A category has been created",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CategoryDTO.class))}),
      @ApiResponse(responseCode = "400",
          description = "Category with the given parent id not found or payload incomplete",
          content = @Content),
      @ApiResponse(responseCode = "409",
          description = "Category with the given name already exists", content = @Content)})
  public CategoryDTO createCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
    Optional<Category> sameCategory = categoryRepository.findByName(categoryDTO.getName());
    if (sameCategory.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "Category with the given name already exists");
    }

    if (categoryDTO.getParentId() != null) {
      Optional<Category> parent = categoryRepository.findById(categoryDTO.getParentId());
      if (parent.isPresent()) {
        return categoryMapper.toCategoryDTO(
            categoryRepository.save(categoryMapper.toCategoryEntity(categoryDTO)));
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Category with the given parent id not found");
      }
    } else {
      //kein Parent_id wurde angegeben
      return categoryMapper.toCategoryDTO(
          categoryRepository.save(categoryMapper.toCategoryEntity(categoryDTO)));
    }

  }
}

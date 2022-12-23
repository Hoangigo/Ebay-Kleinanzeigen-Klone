package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.CategoryDTO;
import de.hs.da.hskleinanzeigen.mapper.CategoryMapper;
import de.hs.da.hskleinanzeigen.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @Autowired
  public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
    this.categoryService = categoryService;
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
  public CategoryDTO createCategory(@RequestBody @Valid CategoryDTO categoryDTO)
       {
    try {
      return
          categoryMapper.toCategoryDTO(
              categoryService.createCategory(categoryMapper.toCategoryEntity(categoryDTO)));
    } catch (Exception e) {
      if (e.getMessage().equals(HttpStatus.CONFLICT.toString())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Category with the given name already exists");
      } else if (e.getMessage().equals(HttpStatus.BAD_REQUEST.toString())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Category with the given parent id not found");
      }
    }
    return null;
  }
}

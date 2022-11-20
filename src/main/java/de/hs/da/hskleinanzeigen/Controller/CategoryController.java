package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.Entities.Category;
import de.hs.da.hskleinanzeigen.Repository.CategoryRepository;
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
public class CategoryController {
  private final CategoryRepository categoryRepository;

  @Autowired
  public CategoryController(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @PostMapping("")
  @ResponseStatus(code = HttpStatus.CREATED)
  public Category createCategory(@RequestBody @Valid Category category) {
    Optional<Category> sameCategory = categoryRepository.findByName(category.getName());
    if(sameCategory.isPresent())
    {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "Category with the given name already exists");
    }//TODO bezieht sich die doppelt category nur auf den Namen? oder (name, parentId)? DDL Category Name Unique?

    if(category.getParent_id() != null) {
      Optional<Category> parent = categoryRepository.findById(category.getParent_id());
      if (parent.isPresent()) {
        return categoryRepository.save(category);
      }
      else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Category with the given parent id not found");
      }
    }
    else {
      //kein Parent_id wurde angegeben
      return categoryRepository.save(category);
    }

  }
}

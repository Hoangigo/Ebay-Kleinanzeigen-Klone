package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Category createCategory(Category category) throws ResponseStatusException {
    Optional<Category> sameCategory = categoryRepository.findByName(category.getName());
    if (sameCategory.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "Category with the given name already exists");
    }

    if (category.getParent_id() != null) {
      //Optional<Category> parent = categoryRepository.findById(category.getParent_id());
      //if (parent.isPresent()) {
      if (categoryRepository.existsById(category.getParent_id())) {
        return categoryRepository.save(category);
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Category with the given parent id not found");
      }
    } else {
      //kein Parent_id wurde angegeben
      return categoryRepository.save(category);
    }
  }
}

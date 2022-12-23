package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.exception.CategoryNameAlreadyExitsException;
import de.hs.da.hskleinanzeigen.exception.PayloadIncorrectException;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Category createCategory(Category category) {
    Optional<Category> sameCategory = categoryRepository.findByName(category.getName());
    if (sameCategory.isPresent()) {
      throw new CategoryNameAlreadyExitsException();
      //throw new Exception(HttpStatus.CONFLICT.toString());
          //"Category with the given name already exists");
    }

    if (category.getParent_id() != null) {
      //Optional<Category> parent = categoryRepository.findById(category.getParent_id());
      //if (parent.isPresent()) {
      if (categoryRepository.existsById(category.getParent_id())) {
        return categoryRepository.save(category);
      } else {
        throw  new PayloadIncorrectException("Category with the given parent id not found");
       // throw new Exception(HttpStatus.BAD_REQUEST.toString());
            //"Category with the given parent id not found");
      }
    } else {
      //kein Parent_id wurde angegeben
      return categoryRepository.save(category);
    }
  }
}

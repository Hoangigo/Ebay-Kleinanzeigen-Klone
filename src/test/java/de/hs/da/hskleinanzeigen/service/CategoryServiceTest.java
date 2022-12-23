package de.hs.da.hskleinanzeigen.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.exception.CategoryNameAlreadyExitsException;
import de.hs.da.hskleinanzeigen.exception.PayloadIncorrectException;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  private CategoryService categoryService;

  @Mock
  private CategoryRepository repository;

  Category generateCategory() {
    Category cat = new Category();
    cat.setName("Tch");
    cat.setId(1);
    //cat.setParent_id(2);

    return cat;
  }

  @BeforeEach
  void setUp() {
    categoryService = new CategoryService(repository);
  }

  @Test
  void createCategory_whenAlreadyExits() {

    Mockito.when(repository.findByName(Mockito.anyString()))
        .thenReturn(Optional.of(generateCategory()));

    Exception exception = assertThrows(CategoryNameAlreadyExitsException.class,
        () -> categoryService.createCategory(generateCategory()));

    assertThat(exception.getMessage()).isEqualTo(CategoryNameAlreadyExitsException.outputMessage);

    Mockito.verify(repository).findByName(generateCategory().getName());
  }

  @Test
  void createCategory_whenParentNotExits() {
    Category catWithParentId = generateCategory();
    catWithParentId.setParent_id(2);

    Mockito.when(repository.findByName(Mockito.anyString()))
        .thenReturn(Optional.ofNullable(null));
    Mockito.when(repository.existsById(Mockito.anyInt())).thenReturn(false);

    Exception exception = assertThrows(PayloadIncorrectException.class,
        () -> categoryService.createCategory(catWithParentId));

    //assertThat(exception.getMessage()).isEqualTo(HttpStatus.BAD_REQUEST.toString());

    Mockito.verify(repository).findByName(generateCategory().getName());
  }

  @Test
  void createCategory_whenEveryThingFineWithParent() {
    Category cat = Mockito.mock(Category.class);
    Category catWithParentId = generateCategory();
    catWithParentId.setParent_id(2);

    Mockito.when(repository.findByName(Mockito.anyString()))
        .thenReturn(Optional.ofNullable(null));
    Mockito.when(repository.existsById(Mockito.anyInt())).thenReturn(true);
    Mockito.when(repository.save(generateCategory())).thenReturn(cat);

    Category category = categoryService.createCategory(catWithParentId);

    assertThat(category).isSameAs(cat);

    Mockito.verify(repository).findByName(generateCategory().getName());
  }

  @Test
  void createCategory_whenEveryThingFineWithoutParent() throws Exception {
    Category cat = Mockito.mock(Category.class);

    Mockito.when(repository.findByName(Mockito.anyString()))
        .thenReturn(Optional.ofNullable(null));
    Mockito.when(repository.save(generateCategory())).thenReturn(cat);

    Category category = categoryService.createCategory(generateCategory());

    assertThat(category).isSameAs(cat);

    Mockito.verify(repository).findByName(generateCategory().getName());
  }
}

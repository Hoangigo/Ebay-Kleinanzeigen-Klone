package de.hs.da.hskleinanzeigen.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.hs.da.hskleinanzeigen.entities.Category;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {"spring.datasource.url=jdbc:tc:mysql:8.0.22:///KLEINANZEIGEN" })
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryJpaTest {

  @Autowired
  private CategoryRepository repository;

  Category generateCategory() {
    Category cat = new Category();
    cat.setName("Tch");
    cat.setId(1);
    //cat.setParent_id(2);
    return cat;
  }

  @Test
  void test_findByName()
  {
    repository.save(generateCategory());
    Optional<Category> category = repository.findByName(generateCategory().getName());
    assertThat(category.isPresent()).isTrue();
    assertThat(category.get().getName()).isEqualTo(generateCategory().getName());
  }
}

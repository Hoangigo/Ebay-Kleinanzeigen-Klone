package de.hs.da.hskleinanzeigen.repository;

import de.hs.da.hskleinanzeigen.entities.Category;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

  Optional<Category> findByName(String name);
}

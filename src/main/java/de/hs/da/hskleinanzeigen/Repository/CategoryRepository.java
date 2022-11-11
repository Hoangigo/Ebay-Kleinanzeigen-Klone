package de.hs.da.hskleinanzeigen.Repository;

import de.hs.da.hskleinanzeigen.Entities.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

}

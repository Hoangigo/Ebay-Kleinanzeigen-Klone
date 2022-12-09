package de.hs.da.hskleinanzeigen.repository;

import de.hs.da.hskleinanzeigen.entities.User;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

  Optional<User> findByEmail(String email);
}

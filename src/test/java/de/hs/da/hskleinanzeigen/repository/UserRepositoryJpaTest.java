package de.hs.da.hskleinanzeigen.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.hs.da.hskleinanzeigen.entities.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest(properties = {"spring.datasource.url=jdbc:tc:mysql:8.0.22:///KLEINANZEIGEN"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryJpaTest {

  @Autowired
  private UserRepository repository;

  //@Rollback
  @Test
  public void test_saveAndfindBy() throws Exception {
    User su = generateUser();
    User se = repository.save(su);
    //System.out.println(se);
    Optional<User> user = this.repository.findById(se.getId());
    assertThat(user.isPresent()).isTrue();
    assertThat(user.get().getEmail()).isEqualTo(generateUser().getEmail());
    //assertThat(se.getId()).isEqualTo(su.getId());
  }

  @Test
  public void test_findByEmail() throws Exception {
    User insertUser = generateUser();
    repository.save(insertUser);
    //System.out.println(se);
    Optional<User> user = this.repository.findByEmail(insertUser.getEmail());
    assertThat(user.isPresent()).isTrue();
    assertThat(user.get().getFirst_name()).isEqualTo(insertUser.getFirst_name());
    //assertThat(se.getId()).isEqualTo(su.getId());
  }

  User generateUser() {
    return new User(110, "tch@h-da.de", "geheim", "Tch",
        "Sobadjo", "123456", "Etoudi",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }
}

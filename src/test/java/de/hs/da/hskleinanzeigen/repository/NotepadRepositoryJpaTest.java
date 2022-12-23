package de.hs.da.hskleinanzeigen.repository;

import static org.assertj.core.api.Assertions.*;

import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Advertisement.AD_TYPE;
import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.entities.Notepad;
import de.hs.da.hskleinanzeigen.entities.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {"spring.datasource.url=jdbc:tc:mysql:8.0.22:///KLEINANZEIGEN"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class NotepadRepositoryJpaTest {

  @Autowired
  private NotepadRepository notepadRepository;

  @Autowired
  private AdvertisementRepository adRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private UserRepository userRepository;

  private Notepad toInsert;

  private Advertisement insertAD;

  private User insertUser;

  Category generateCategory(String name) {
    Category cat = new Category();
    cat.setName(name);
    cat.setId(1);
    //cat.setParent_id(2);
    return cat;
  }

  User generateUser(int id, String mail) {
    return new User(id, mail, "geheim", "Tch",
        "Sobadjo", "123456", "Etoudi",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  //TODO warning here? why always same values?
  Advertisement generateAD(User user, Category category, String title, AD_TYPE type) {
    return new Advertisement(1, type, category,
        user, title, "description", 10, "location",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  Notepad generateNotepad(User user, Advertisement ad, String note) {
    return new Notepad(123, user, ad, note,
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  @BeforeEach
  void setUp() {
    insertUser = userRepository.save(generateUser(54, "t@moses.de"));
    Category insertCategory = categoryRepository.save(generateCategory("Corona"));
    insertAD = adRepository.save(generateAD(insertUser, insertCategory, "title", AD_TYPE.REQUEST));
    //System.out.println(insertAD);
    toInsert = generateNotepad(insertUser, insertAD, "note");
    toInsert = notepadRepository.save(toInsert);
  }


  @Test
  void test_findByUserIdAndAdId() {
    Optional<Notepad> notepad = notepadRepository.findByUserIdAndAdId(insertUser.getId(),
        insertAD.getId());
    assertThat(notepad.isPresent()).isTrue();
    assertThat(notepad.get().getNote()).isEqualTo(toInsert.getNote());
  }

  @Test
  void test_findByUserId() {
    List<Notepad> result = notepadRepository.findByUserId(insertUser.getId());
    assertThat(result).hasSize(1);
  }

  @Test
  void test_deleteByUserIdAndAdId() {
    notepadRepository.deleteByUserIdAndAdId(insertUser.getId(), insertAD.getId());
    List<Notepad> result = notepadRepository.findByUserId(insertUser.getId());
    assertThat(result).isEmpty();

  }
}

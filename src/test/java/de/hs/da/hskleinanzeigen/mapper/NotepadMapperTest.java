package de.hs.da.hskleinanzeigen.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.hs.da.hskleinanzeigen.dto.NotepadGetDTO;
import de.hs.da.hskleinanzeigen.dto.NotepadPutDTO;
import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Notepad;
import de.hs.da.hskleinanzeigen.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // we should run the whole App in order to use Autowired
public class NotepadMapperTest {

  @Autowired
  NotepadMapper mapper;

  @Autowired
  AdvertisementMapper adMapper;

  Notepad fromNotepad;

  Notepad generateNotepad(User user, Advertisement ad, String note) {
    return new Notepad(123, user, ad, note,
        new java.sql.Timestamp(System.currentTimeMillis()));
  }


  @BeforeEach
  void setUp() {
    fromNotepad = generateNotepad(new User(78, "poi@h-da.de", "xxxxZZZ", "Pi",
            "RE", "123456", "Darmstadt",
            new java.sql.Timestamp(System.currentTimeMillis())),
        AdvertisementMapperTest.generateAd(),
        "Meine Note..xD");
  }

  @Test
  void toNotepadPutDTO() {
    NotepadPutDTO putDTO = mapper.toNotepadPutDTO(fromNotepad);

    assertThat(putDTO.getId()).isEqualTo(fromNotepad.getId());
    assertThat(putDTO.getNote()).isEqualTo(fromNotepad.getNote());
    assertThat(putDTO.getAdvertisementId()).isEqualTo(fromNotepad.getAdvertisement().getId());
    assertThat(putDTO.getUserId()).isEqualTo(fromNotepad.getUser().getId());
  }

  @Test
  void toNotepadGetDTO() {
    NotepadGetDTO getDTO = mapper.toNotepadGetDTO(fromNotepad);

    assertThat(getDTO.getId()).isEqualTo(fromNotepad.getId());
    assertThat(getDTO.getNote()).isEqualTo(fromNotepad.getNote());
    assertThat(getDTO.getAdvertisement()).isEqualTo(
        adMapper.toADDTO(fromNotepad.getAdvertisement()));
  }
}

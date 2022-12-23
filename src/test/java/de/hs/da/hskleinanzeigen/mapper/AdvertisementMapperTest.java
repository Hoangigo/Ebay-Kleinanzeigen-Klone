package de.hs.da.hskleinanzeigen.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.hs.da.hskleinanzeigen.dto.AdvertisementDTO;
import de.hs.da.hskleinanzeigen.dto.CategoryDTO;
import de.hs.da.hskleinanzeigen.dto.UserDTO;
import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Advertisement.AD_TYPE;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest //Hinweis: AdvertisementMapper inscludes CategoryMapper and UserMapper => Autowired
public class AdvertisementMapperTest {
  @Autowired
  AdvertisementMapper mapper;
  CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
  UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  Advertisement fromAD;
  AdvertisementDTO fromADDTO;

  static  Advertisement generateAd() {
    return new Advertisement(1, AD_TYPE.OFFER, CategoryMapperTest.generateCategory("Corona"),
        UserMapperTest.generateUser(), "title", "description", 10, "location",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  @BeforeEach
  void  setUp() {
    fromAD = generateAd();
    fromADDTO = new AdvertisementDTO(15, AD_TYPE.REQUEST,
        new CategoryDTO(11, 17, "ferien5"),
        new UserDTO(50, "test@de.de",
            "geheimnis", "wer", "bist", "phone", "location"),
        "title2", "description2", 100, "location2");
  }

  @Test
  void toADEntity() {
    Advertisement ad = mapper.toADEntity(fromADDTO);

    assertThat(ad.getId()).isEqualTo(fromADDTO.getId());
    assertThat(ad.getDescription()).isEqualTo(fromADDTO.getDescription());
    assertThat(ad.getTitle()).isEqualTo(fromADDTO.getTitle());
    assertThat(ad.getType()).isEqualTo(fromADDTO.getType());
    assertThat(ad.getPrice()).isEqualTo(fromADDTO.getPrice());
    assertThat(categoryMapper.toCategoryDTO(ad.getCategory())).isEqualTo(fromADDTO.getCategory());
    assertThat(userMapper.toUserDTO(ad.getUser())).isEqualTo(fromADDTO.getUser());
  }

  @Test
  void toADDTO() {
    AdvertisementDTO dto = mapper.toADDTO(fromAD);

    assertThat(dto.getId()).isEqualTo(fromAD.getId());
    assertThat(dto.getDescription()).isEqualTo(fromAD.getDescription());
    assertThat(dto.getTitle()).isEqualTo(fromAD.getTitle());
    assertThat(dto.getType()).isEqualTo(fromAD.getType());
    assertThat(dto.getPrice()).isEqualTo(fromAD.getPrice());
    assertThat(dto.getCategory()).isEqualTo(categoryMapper.toCategoryDTO(fromAD.getCategory()));
    assertThat(dto.getUser()).isEqualTo(userMapper.toUserDTO(fromAD.getUser()));
  }
}

package de.hs.da.hskleinanzeigen.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.hs.da.hskleinanzeigen.dto.UserDTO;
import de.hs.da.hskleinanzeigen.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class UserMapperTest {

  UserMapper mapper = Mappers.getMapper(UserMapper.class);

  static User generateUser() {
    return new User(110, "tch@h-da.de", "geheim", "Tch",
        "Sobadjo", "123456", "Etoudi",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  User fromUser;
  UserDTO fromUserDTO;

  @BeforeEach
  void setUp() {
    fromUser = generateUser();
    fromUserDTO = new UserDTO(50, "test@de.de",
        "geheimnis", "wer", "bist", "phone", "location");
  }

  @Test
  void toUserEntity()
  {
    User user = mapper.toUserEntity(fromUserDTO);

    assertThat(user.getFirst_name()).isEqualTo(fromUserDTO.getFirstName());
    assertThat(user.getId()).isEqualTo(fromUserDTO.getId());
    assertThat(user.getEmail()).isEqualTo(fromUserDTO.getEmail());
    assertThat(user.getLocation()).isEqualTo(fromUserDTO.getLocation());
    assertThat(user.getPhone()).isEqualTo(fromUserDTO.getPhone());
    assertThat(user.getLast_name()).isEqualTo(fromUserDTO.getLastName());
    assertThat(user.getPassword()).isEqualTo(fromUserDTO.getPassword());
  }

  @Test
  void toUserDTO()
  {
    UserDTO userDTO = mapper.toUserDTO(fromUser);

    assertThat(userDTO.getFirstName()).isEqualTo(fromUser.getFirst_name());
    assertThat(userDTO.getId()).isEqualTo(fromUser.getId());
    assertThat(userDTO.getEmail()).isEqualTo(fromUser.getEmail());
    assertThat(userDTO.getLocation()).isEqualTo(fromUser.getLocation());
    assertThat(userDTO.getPhone()).isEqualTo(fromUser.getPhone());
    assertThat(userDTO.getLastName()).isEqualTo(fromUser.getLast_name());
    assertThat(userDTO.getPassword()).isEqualTo(null);
  }
}

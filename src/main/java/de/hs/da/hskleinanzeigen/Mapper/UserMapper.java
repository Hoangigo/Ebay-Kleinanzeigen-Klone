package de.hs.da.hskleinanzeigen.Mapper;

import de.hs.da.hskleinanzeigen.DTO.UserDTO;
import de.hs.da.hskleinanzeigen.Entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.WARN)
public interface UserMapper {

  @Mapping(target = "firstName", source = "userEntity.first_name")
  @Mapping(target = "lastName", source = "userEntity.last_name")
  @Mapping(target = "password", ignore = true)
  UserDTO toUserDTO(User userEntity);

  @Mapping(target = "first_name", source = "userDTO.firstName")
  @Mapping(target = "last_name", source = "userDTO.lastName")
  @Mapping(target = "created",
      expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
  User toUserEntity(UserDTO userDTO);
}

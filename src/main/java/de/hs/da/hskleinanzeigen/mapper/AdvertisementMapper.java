package de.hs.da.hskleinanzeigen.mapper;

import de.hs.da.hskleinanzeigen.dto.AdvertisementDTO;
import de.hs.da.hskleinanzeigen.entities.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.WARN,
    uses = {CategoryMapper.class, UserMapper.class})
public interface AdvertisementMapper {

  @Mapping(target = "created",
      expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
  Advertisement toADEntity(AdvertisementDTO adDTO);

  AdvertisementDTO toADDTO(Advertisement ad);
}

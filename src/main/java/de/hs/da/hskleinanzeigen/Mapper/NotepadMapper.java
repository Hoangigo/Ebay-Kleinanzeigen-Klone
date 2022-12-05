package de.hs.da.hskleinanzeigen.Mapper;

import de.hs.da.hskleinanzeigen.DTO.NotepadGetDTO;
import de.hs.da.hskleinanzeigen.DTO.NotepadPutDTO;
import de.hs.da.hskleinanzeigen.Entities.Notepad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.WARN,
    uses = {AdvertisementMapper.class})
public interface NotepadMapper {

  //Notepad toNotepadEntity(NotepadPutDTO noteDTO);

  @Mapping(target = "advertisementId", source = "notepad.advertisement.id")
  @Mapping(target = "userId", source = "notepad.user.id")
  NotepadPutDTO toNotepadPutDTO(Notepad notepad);


  NotepadGetDTO toNotepadGetDTO(Notepad notepad);
}

package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.DTO.NotepadGetDTO;
import de.hs.da.hskleinanzeigen.DTO.NotepadPutDTO;
import de.hs.da.hskleinanzeigen.Entities.Advertisement;
import de.hs.da.hskleinanzeigen.Entities.Notepad;
import de.hs.da.hskleinanzeigen.Entities.User;
import de.hs.da.hskleinanzeigen.Mapper.NotepadMapper;
import de.hs.da.hskleinanzeigen.Repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.Repository.NotepadRepository;
import de.hs.da.hskleinanzeigen.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Notepad", description = "Read, set and update Notepad, also delete entry from Notepad")
public class NotepadController {

  private final NotepadRepository notepadRepository;
  private final UserRepository userRepository;
  private final NotepadMapper notepadMapper;
  private final AdvertisementRepository adRepository;

  @Autowired
  public NotepadController(NotepadRepository notepadRepository, NotepadMapper notepadMapper,
      UserRepository userRepository, AdvertisementRepository adRepository) {
    this.notepadRepository = notepadRepository;
    this.notepadMapper = notepadMapper;
    this.userRepository = userRepository;
    this.adRepository = adRepository;
  }

  @PutMapping(consumes = "application/json", path = "/api/users/{userId}/notepad")
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(summary = "Add an AD to an User Notepad")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Notepad of the given user has successfully been updated",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = NotepadPutDTO.class))}),
      @ApiResponse(responseCode = "400", content = @Content,
          description = "Advertisement or User with the given ids not found OR payload incomplete")})
  public NotepadPutDTO addADtoNotepad(@Valid @RequestBody NotepadPutDTO note,
      @Parameter(description = "id of user whom notepad should be updated")
      @PathVariable Integer userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isPresent()) {
      Optional<Advertisement> advertisement = adRepository.findById(note.getAdvertisementId());
      if (advertisement.isPresent()) {
        Optional<Notepad> notepadOptional = notepadRepository.findByUserIdAndAdId(userId,
            note.getAdvertisementId());
        if (notepadOptional.isPresent()) {
          //TODO Mit Blick auf performance, besser eigene update schreiben? and existById statt findById?
          Notepad notepad = notepadOptional.get();
          notepad.setNote(note.getNote());
          return notepadMapper.toNotepadPutDTO(notepadRepository.save(notepad));
        } else {
          Notepad notepad = new Notepad();
          notepad.setAdvertisement(advertisement.get());
          notepad.setUser(user.get());
          notepad.setNote(note.getNote());
          notepad.setCreated(new java.sql.Timestamp(System.currentTimeMillis()));
          return notepadMapper.toNotepadPutDTO(notepadRepository.save(notepad));
        }
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Advertisement with the given id not found");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "User with the given id not found");
    }
  }

  @GetMapping(path = "/api/users/{userId}/notepad", produces = "application/json")
  @Operation(summary = "Get Notepad of a given user")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Return the whole Notepad of the given user",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = NotepadGetDTO.class))}),
      @ApiResponse(responseCode = "404",
          description = "User with the given id not found", content = @Content),
      @ApiResponse(responseCode = "204", content = @Content,
          description = "No entry found on notepad for the given user")})
  public List<NotepadGetDTO> getNotepadByUser(@Parameter(description = "id of user")
  @PathVariable Integer userId) {
    //Optional<User> user = userRepository.findById(userId);
    //TODO untere Variante performanter?
    if (userRepository.existsById(userId)) {
      List<Notepad> notepadList = notepadRepository.findByUserId(userId);
      if (!notepadList.isEmpty()) {
        return notepadList.stream()
            .map(notepad -> notepadMapper.toNotepadGetDTO(notepad)).collect(Collectors.toList());
      }
      throw new ResponseStatusException(HttpStatus.NO_CONTENT,
          "No entry found on notepad for the given user");
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
  }


  @DeleteMapping(path = "/api/users/{userId}/notepad/{adId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT) //TODO wirklich so gewollt?
  @Operation(summary = "Delete an AD from Notepad of a given user")
  @ApiResponses({
      @ApiResponse(responseCode = "204", content = @Content,
          description = "Ad has been successfully removed from Notepad"),
      @ApiResponse(responseCode = "404",
          description = "User or Advertisement with the given id not found", content = @Content)})
  public void deleteNotepad(
      @Parameter(description = "id of user") @PathVariable Integer userId,
      @Parameter(description = "id of the advertisement to be deleted") @PathVariable Integer adId){
    //Optional<User> user = userRepository.findById(userId);
    //if (user.isPresent()) {
    if (userRepository.existsById(userId)) {
      //Optional<Advertisement> advertisement = adRepository.findById(adId);
      if (adRepository.existsById(adId)) {
        //Optional<Notepad> notepad = notepadRepository.findByUserIdAndAdId(userId,adId);
        //
        notepadRepository.deleteByUserIdAndAdId(userId, adId);
        return;
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Advertisement with the given id not found");
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
  }

}

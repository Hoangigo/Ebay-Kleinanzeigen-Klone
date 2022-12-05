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
  public NotepadPutDTO addADtoNotepad(@Valid @RequestBody NotepadPutDTO note,
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
  public List<NotepadGetDTO> getNotepadByUser(@PathVariable Integer userId) {
    //Optional<User> user = userRepository.findById(userId);
    //TODO untere Variante performanter?
    if (userRepository.existsById(userId)) {
      List<Notepad> notepadList = notepadRepository.findByUserId(userId);
      if (!notepadList.isEmpty()) {
        return notepadList.stream()
            .map(notepad -> notepadMapper.toNotepadGetDTO(notepad)).collect(Collectors.toList());
      }
      throw new ResponseStatusException(HttpStatus.NO_CONTENT,
          "No notepad found for the given user");
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
  }


  @DeleteMapping(path = "/api/users/{userId}/notepad/{adId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT) //TODO wirklich so gewollt?
  public void deleteNotepad(@PathVariable Integer userId, @PathVariable Integer adId) {
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

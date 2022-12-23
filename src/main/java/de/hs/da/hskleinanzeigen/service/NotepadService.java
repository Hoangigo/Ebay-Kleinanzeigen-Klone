package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.dto.NotepadPutDTO;
import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Notepad;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.exception.AdNotFoundException;
import de.hs.da.hskleinanzeigen.exception.NoContentException;
import de.hs.da.hskleinanzeigen.exception.UserNotFoundException;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.NotepadRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotepadService {

  private final NotepadRepository notepadRepository;
  private final UserRepository userRepository;
  private final AdvertisementRepository adRepository;


  public Notepad addADtoNotepad(NotepadPutDTO note, Integer userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isPresent()) {
      Optional<Advertisement> advertisement = adRepository.findById(note.getAdvertisementId());
      if (advertisement.isPresent()) {
        Optional<Notepad> notepadOptional = notepadRepository.findByUserIdAndAdId(userId,
            note.getAdvertisementId());
        if (notepadOptional.isPresent()) { //AD schon vorhanden!!! nur note  updaten?
          // Mit Blick auf performance, besser eigene update schreiben? and existById statt findById?
          Notepad notepad = notepadOptional.get();
          notepad.setNote(note.getNote());
          return notepadRepository.save(notepad);
        } else {
          Notepad notepad = new Notepad();
          notepad.setAdvertisement(advertisement.get());
          notepad.setUser(user.get());
          notepad.setNote(note.getNote());
          notepad.setCreated(new java.sql.Timestamp(System.currentTimeMillis()));
          return notepadRepository.save(notepad);
        }
      } else {
        /*throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Advertisement with the given id not found");*/
        throw new AdNotFoundException();
      }
    } else {
      /*throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "User with the given id not found");*/
      throw new UserNotFoundException();
    }
  }

  public List<Notepad> getNotepadByUser(Integer userId) {
    //Optional<User> user = userRepository.findById(userId);
    //untere Variante performanter? JA, exits lädt nicht das ganze
    if (userRepository.existsById(userId)) {
      List<Notepad> notepadList = notepadRepository.findByUserId(userId);
      if (!notepadList.isEmpty()) {
        return notepadList;
      }
      /*throw new ResponseStatusException(HttpStatus.NO_CONTENT,
          "No entry found on notepad for the given user");*/
      throw new NoContentException("No entry found on notepad for the given user");
    }
    /*throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");*/
    throw new UserNotFoundException();
  }

  public void deleteNotepad(Integer userId, Integer adId) {
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
      /*throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Advertisement with the given id not found");*/
      throw new AdNotFoundException();
    }
    //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
    throw new UserNotFoundException();
  }
}

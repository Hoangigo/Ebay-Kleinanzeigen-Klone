package de.hs.da.hskleinanzeigen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.hs.da.hskleinanzeigen.dto.NotepadPutDTO;
import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Notepad;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.NotepadRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class NotepadServiceTest {

  private NotepadService service;

  @Mock
  private NotepadRepository notRepository;

  @Mock
  private AdvertisementRepository adRepository;

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    service = new NotepadService(notRepository, userRepository, adRepository);
  }

  @Test
  void getNotepadByUser_whenIdNotMatch() {
    Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(false);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.getNotepadByUser(Mockito.anyInt()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(exception.getReason()).isEqualTo("User with the given id not found");

    Mockito.verify(userRepository).existsById(Mockito.anyInt());
  }

  @Test
  void getNotepadByUser_whenNoNotepadForUser() {
    Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
    Mockito.when(notRepository.findByUserId(Mockito.anyInt())).thenReturn(new ArrayList<>());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.getNotepadByUser(Mockito.anyInt()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(exception.getReason()).isEqualTo("No entry found on notepad for the given user");

    Mockito.verify(userRepository).existsById(Mockito.anyInt());
  }

  @Test
  void getNotepadByUser_whenUserhasNotepad() {
    Notepad n1 = new Notepad();
    Notepad n2 = new Notepad();
    n2.setNote("TEST");
    Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
    Mockito.when(notRepository.findByUserId(Mockito.anyInt())).thenReturn(Arrays.asList(n1, n2));

    List<Notepad> result = service.getNotepadByUser(Mockito.anyInt());

    assertThat(result).hasSize(2);
    assertThat(result.get(1)).isEqualTo(n2);

    Mockito.verify(userRepository).existsById(Mockito.anyInt());
  }

  @Test
  void deleteNotepad_whenUserNotFound() {
    Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(false);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.deleteNotepad(Mockito.anyInt(), 2));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(exception.getReason()).isEqualTo("User with the given id not found");

    Mockito.verify(userRepository).existsById(Mockito.anyInt());
  }

  @Test
  void deleteNotepad_whenAdNotFound() {
    Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
    Mockito.when(adRepository.existsById(Mockito.anyInt())).thenReturn(false);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.deleteNotepad(10, Mockito.anyInt()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(exception.getReason()).isEqualTo("Advertisement with the given id not found");

    Mockito.verify(userRepository).existsById(Mockito.anyInt());
    Mockito.verify(adRepository).existsById(Mockito.anyInt());
  }

  @Test
  void deleteNotepad_whenNotepadBeDeleted() {
    Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
    Mockito.when(adRepository.existsById(Mockito.anyInt())).thenReturn(true);

    service.deleteNotepad(10, 20);

    Mockito.verify(userRepository).existsById(Mockito.anyInt());
    Mockito.verify(adRepository).existsById(Mockito.anyInt());
    Mockito.verify(notRepository).deleteByUserIdAndAdId(10, 20);
  }

  @Test
  void addADtoNotepad_whenUserNotFound() {
    NotepadPutDTO insertNotepad = Mockito.mock(NotepadPutDTO.class);
    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.addADtoNotepad(insertNotepad, Mockito.anyInt()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(exception.getReason()).isEqualTo("User with the given id not found");

    Mockito.verify(userRepository).findById(Mockito.anyInt());
  }

  @Test
  void addADtoNotepad_whenAdNotFound() {
    NotepadPutDTO insertNotepad = Mockito.mock(NotepadPutDTO.class);
    User user = Mockito.mock(User.class);
    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
    Mockito.when(adRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.addADtoNotepad(insertNotepad, Mockito.anyInt()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(exception.getReason()).isEqualTo("Advertisement with the given id not found");

    Mockito.verify(userRepository).findById(Mockito.anyInt());
    Mockito.verify(adRepository).findById(insertNotepad.getAdvertisementId());
  }

  @Test
  void addADtoNotepad_whenNewNotepad() {
    NotepadPutDTO insertNotepad = Mockito.mock(NotepadPutDTO.class);
    User user = Mockito.mock(User.class);
    Advertisement ad = Mockito.mock(Advertisement.class);
    Notepad expected = Mockito.mock(Notepad.class);

    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
    Mockito.when(adRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(ad));
    Mockito.when(notRepository.findByUserIdAndAdId(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(Optional.ofNullable(null));
    Mockito.when(notRepository.save(Mockito.any())).thenReturn(expected);

    Notepad result = service.addADtoNotepad(insertNotepad, Mockito.anyInt());

    assertThat(result.getNote()).isEqualTo(expected.getNote());
    //assertThat(result.getAdvertisement().getId()).isEqualTo(expected.getAdvertisement().getId());
    assertThat(result.getId()).isEqualTo(expected.getId());

    Mockito.verify(userRepository).findById(Mockito.anyInt());
    Mockito.verify(adRepository).findById(insertNotepad.getAdvertisementId());
    Mockito.verify(notRepository).findByUserIdAndAdId(Mockito.anyInt(), Mockito.anyInt());
  }

  @Test
  void addADtoNotepad_whenNotepadAlreadyExits() {
    NotepadPutDTO insertNotepad = Mockito.mock(NotepadPutDTO.class);
    User user = Mockito.mock(User.class);
    Advertisement ad = Mockito.mock(Advertisement.class);
    Notepad expected = Mockito.mock(Notepad.class);
    //expected.setAdvertisement(ad);

    Notepad existingNotepad = Mockito.mock(Notepad.class);

    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
    Mockito.when(adRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(ad));
    Mockito.when(notRepository.findByUserIdAndAdId(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(Optional.ofNullable(existingNotepad));
    Mockito.when(notRepository.save(Mockito.any())).thenReturn(expected);

    Notepad result = service.addADtoNotepad(insertNotepad, Mockito.anyInt());

    assertThat(result.getNote()).isEqualTo(expected.getNote());
    //assertThat(result.getAdvertisement().getId()).isEqualTo(expected.getAdvertisement().getId());
    //assertThat(result.getAdvertisement().getId()).isEqualTo(ad.getId());
    assertThat(result.getId()).isEqualTo(expected.getId());

    Mockito.verify(userRepository).findById(Mockito.anyInt());
    Mockito.verify(adRepository).findById(insertNotepad.getAdvertisementId());
    Mockito.verify(notRepository).findByUserIdAndAdId(Mockito.anyInt(), Mockito.anyInt());
  }
}

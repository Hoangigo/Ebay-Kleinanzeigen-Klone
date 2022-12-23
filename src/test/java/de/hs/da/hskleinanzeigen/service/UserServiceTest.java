package de.hs.da.hskleinanzeigen.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.exception.NoContentException;
import de.hs.da.hskleinanzeigen.exception.PayloadIncorrectException;
import de.hs.da.hskleinanzeigen.exception.UserEmailAlreadyExitsException;
import de.hs.da.hskleinanzeigen.exception.UserNotFoundException;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private UserService userService;

  @Mock
  private UserRepository repository;

  final int validPageSize = 10;
  final int invalidPageSize = -5;
  final int validPageStart = 1;
  final int invalidPageStart = -2;

  User generateUser() {
    return new User(110, "tch@h-da.de", "geheim", "Tch",
        "Sobadjo", "123456", "Etoudi",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  @BeforeEach
  void setUp() {
    userService = new UserService(repository);
  }

  @Test
  void createUser_whenEmailAlreadyExits() {

    Mockito.when(repository.findByEmail(Mockito.anyString()))
        .thenReturn(Optional.ofNullable(generateUser()));

    UserEmailAlreadyExitsException exception = assertThrows(UserEmailAlreadyExitsException.class,
        () -> userService.createUser(generateUser()));

    assertThat(exception.getMessage()).isEqualTo(UserEmailAlreadyExitsException.outputMessage);

    Mockito.verify(repository).findByEmail(generateUser().getEmail());
  }

  @Test
  void createUser_whenAllFine() {
    User newUser = Mockito.mock(User.class);

    Mockito.when(repository.findByEmail(Mockito.anyString()))
        .thenReturn(Optional.ofNullable(null));
    Mockito.when(repository.save(generateUser()))
        .thenReturn(newUser);

    User user = userService.createUser(generateUser());

    assertThat(user).isSameAs(newUser);

    Mockito.verify(repository).findByEmail(generateUser().getEmail());
  }

  @Test
  void readOneUser_whenUserExits() {
    User existingUser = Mockito.mock(User.class);

    Mockito.when(repository.findById(Mockito.anyInt()))
        .thenReturn(Optional.ofNullable(existingUser));

    assertThat(userService.readOneUser(generateUser().getId())).isSameAs(existingUser);

    Mockito.verify(repository).findById(generateUser().getId());
  }

  @Test
  void readOneUser_whenUserNotExits() {
    Mockito.when(repository.findById(Mockito.anyInt()))
        .thenReturn(Optional.ofNullable(null));

    Exception exception = assertThrows(UserNotFoundException.class,
        () -> userService.readOneUser(generateUser().getId()));

    assertThat(exception.getMessage()).isEqualTo(UserNotFoundException.outputMessage);

    Mockito.verify(repository).findById(generateUser().getId());
  }

  @Test
  void readUsers_whenPageParameterInvalid() {
    assertThrows(PayloadIncorrectException.class,
        () -> userService.readUsers(invalidPageStart, validPageSize));
    //assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThrows(PayloadIncorrectException.class,
        () -> userService.readUsers(validPageStart, invalidPageSize));
    // assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThrows(PayloadIncorrectException.class,
        () -> userService.readUsers(invalidPageStart, invalidPageSize));
    // assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void readUsers_whenNoContent() {
    Page<User> emptyPage = Page.empty();
    Mockito.when(repository.findAll((Pageable) Mockito.any())).thenReturn(emptyPage);
    Exception exception = assertThrows(NoContentException.class,
        () -> userService.readUsers(validPageStart, validPageSize));

    assertThat(exception.getMessage()).isEqualTo("Such User entries not found");
  }

  @Test
  void readUsers_whenOK() {
    /*List<User> userList = new ArrayList<>();

    Pageable page = PageRequest.of(validPageStart, validPageSize,
        Sort.by("created").ascending());
    Page<User> result = new PageImpl<>(List.of(generateUser()), page, 2);*/
    Page<User> expected = Mockito.mock(Page.class);

    Mockito.when(repository.findAll((Pageable) Mockito.any())).thenReturn(expected);

    Page<User> result = userService.readUsers(validPageStart, validPageSize);

    assertThat(result).isEqualTo(expected);
  }
}

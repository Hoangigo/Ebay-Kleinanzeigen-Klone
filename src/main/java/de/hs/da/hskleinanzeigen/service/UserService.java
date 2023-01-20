package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.exception.NoContentException;
import de.hs.da.hskleinanzeigen.exception.PayloadIncorrectException;
import de.hs.da.hskleinanzeigen.exception.UserEmailAlreadyExitsException;
import de.hs.da.hskleinanzeigen.exception.UserNotFoundException;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User createUser(User user) {
    Optional<User> doubleEmail = userRepository.findByEmail(user.getEmail());
    if (doubleEmail.isPresent()) {
      throw new UserEmailAlreadyExitsException();
      /*throw new ResponseStatusException(HttpStatus.CONFLICT,
          "This email is already used by an other User");*/
    }
    return userRepository.save(user);
  }

  public User readOneUser(final Integer id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      return user.get();
    }
    //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
    throw new UserNotFoundException();
  }

  public Page<User> readUsers(int pageStart, int pageSize) {
    if (pageSize < 1 || pageStart < 0) {
      /*throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Parameter are not valid! Notice: size > 1 and start >= 0");*/
      throw new PayloadIncorrectException(
          "Parameter are not valid! Notice: size > 1 and start >= 0");
    }

    Pageable indexOfPageAndNumberOfElements = PageRequest.of(pageStart, pageSize,
        Sort.by("created").ascending());

    Page<User> result = userRepository.findAll(indexOfPageAndNumberOfElements);
    if (result.isEmpty()) {
      //throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Such User entries not found");
      throw new NoContentException("Such User entries not found");
    }

    return result;
  }
}

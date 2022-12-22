package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User createUser(User user) throws ResponseStatusException {
    Optional<User> doubleEmail = userRepository.findByEmail(user.getEmail());
    if (doubleEmail.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "This email is already used by an other User");
    }
    return userRepository.save(user);
  }

  public User readOneUser(final Integer id) throws ResponseStatusException {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      return user.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
  }

  public Page<User> readUsers(int pageStart, int pageSize) throws ResponseStatusException {
    if (pageSize < 1 || pageStart < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Parameter are not valid! Notice: size > 1 and start >= 0");
    }

    Pageable indexOfPageAndNumberOfElements = PageRequest.of(pageStart, pageSize,
        Sort.by("created").ascending());

    Page<User> result = userRepository.findAll(indexOfPageAndNumberOfElements);
    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Such User entries not found");
    }

    return result;
  }
}

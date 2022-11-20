package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.Entities.User;
import de.hs.da.hskleinanzeigen.Repository.UserRepository;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

  @Autowired
  UserRepository userRepository;

  @PostMapping("")
  @ResponseStatus(code = HttpStatus.CREATED)
  public User createUser(@RequestBody @Valid User user) {
    Optional<User> doubleEmail = userRepository.findByEmail(user.getEmail());
    if (doubleEmail.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "This email is already used by an other User");
    }
    return userRepository.save(user);
  }

  @GetMapping(path = "/{id}")
  public User readOneUser(@PathVariable Integer id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      return user.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
  }

  @GetMapping(path = "")
  public Page<User> readUsers(@RequestParam(name = "pageStart", required = true) int pageStart,
      @RequestParam(name = "pageSize", required = true) int pageSize) {
    if ((pageSize < 1) || (pageStart < 0)) //TODO ist 0 für pageSize erlaubt?
    {
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

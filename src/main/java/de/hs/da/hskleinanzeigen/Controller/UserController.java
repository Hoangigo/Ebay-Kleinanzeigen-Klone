package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.DTO.UserDTO;
import de.hs.da.hskleinanzeigen.Entities.User;
import de.hs.da.hskleinanzeigen.Mapper.UserMapper;
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


  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public UserController(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @PostMapping(consumes = "application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  public UserDTO createUser(@RequestBody @Valid UserDTO userDTO) {
    Optional<User> doubleEmail = userRepository.findByEmail(userDTO.getEmail());
    if (doubleEmail.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "This email is already used by an other User");
    }
    return userMapper.toUserDTO(userRepository.save(userMapper.toUserEntity(userDTO)));
  }

  @GetMapping(produces = "application/json", path = "/{id}")
  public UserDTO readOneUser(@PathVariable final Integer id) {
    Optional<User> user = (userRepository.findById(id));
    if (user.isPresent()) {
      return userMapper.toUserDTO(user.get());
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
  }

  @GetMapping(produces = "application/json", path = "")
  public Page<UserDTO> readUsers(@RequestParam(name = "pageStart", required = true) int pageStart,
      @RequestParam(name = "pageSize", required = true) int pageSize) {
    if ((pageSize < 1) || (pageStart < 0))
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
    Page<UserDTO> resultDTO = result.map(user -> userMapper.toUserDTO(user));

    return resultDTO;
  }

}

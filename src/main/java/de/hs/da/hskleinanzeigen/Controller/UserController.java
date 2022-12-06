package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.DTO.UserDTO;
import de.hs.da.hskleinanzeigen.Entities.User;
import de.hs.da.hskleinanzeigen.Mapper.UserMapper;
import de.hs.da.hskleinanzeigen.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "Read and set users and their properties")
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
  @Operation(summary = "Create a new user")
  @ApiResponses({ //
      @ApiResponse(responseCode = "201", description = "A new user has been created"),
      @ApiResponse(responseCode = "409",
          description = "The given email is already used by an other User")})
  public UserDTO createUser(@Parameter(description = "Infos of the user to be created")
  @RequestBody @Valid UserDTO userDTO) {
    Optional<User> doubleEmail = userRepository.findByEmail(userDTO.getEmail());
    if (doubleEmail.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "This email is already used by an other User");
    }
    return userMapper.toUserDTO(userRepository.save(userMapper.toUserEntity(userDTO)));
  }

  @GetMapping(produces = "application/json", path = "/{id}")
  @Operation(summary = "Returns a specific user by its identifier.")
  @ApiResponses({ //
      @ApiResponse(responseCode = "200", description = "User with the given id was found"),
      @ApiResponse(responseCode = "404", description = "No user found")
  })
  public UserDTO readOneUser(
      @Parameter(description = "id of user to be searched") @PathVariable final Integer id) {
    Optional<User> user = (userRepository.findById(id));
    if (user.isPresent()) {
      return userMapper.toUserDTO(user.get());
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with the given id not found");
  }

  @GetMapping(produces = "application/json", path = "")
  @Operation(summary = "Returns a Page of users.")
  @ApiResponses({ //
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "400",
          description = "Parameter are not valid! Notice: size > 1 and start >= 0")})
  public Page<UserDTO> readUsers(
      @Parameter(description = "index of the page to be shown")
      @RequestParam(name = "pageStart", required = true) int pageStart,
      @Parameter(description = "maximal number of users on a Page")
      @RequestParam(name = "pageSize", required = true) int pageSize) {
    if ((pageSize < 1) || (pageStart < 0)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Parameter are not valid! Notice: size > 1 and start >= 0");
    }

    Pageable indexOfPageAndNumberOfElements = PageRequest.of(pageStart, pageSize,
        Sort.by("created").ascending());

    Page<User> result = userRepository.findAll(indexOfPageAndNumberOfElements);
    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Such User entries not found");
    }

    return result.map(user -> userMapper.toUserDTO(user));
  }

}

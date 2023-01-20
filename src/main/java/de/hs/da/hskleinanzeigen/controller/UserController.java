package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.UserDTO;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.mapper.UserMapper;
import de.hs.da.hskleinanzeigen.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/users")
@Tag(name = "User", description = "Read and set users and their properties")
public class UserController {


  private final UserService userService;
  private final UserMapper userMapper;

  @Autowired
  public UserController(UserService userService, UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @PostMapping(consumes = "application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @Operation(summary = "Create a new user")
  @ApiResponses({ //
      @ApiResponse(responseCode = "201", description = "A new user has been created",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserDTO.class))}),
      @ApiResponse(responseCode = "400",
          description = "Payload incomplete or validation failed", content = @Content),
      @ApiResponse(responseCode = "409",
          description = "The given email is already used by an other User", content = @Content)})
  @CachePut(value = "Users", key = "#result.id")
  public UserDTO createUser(@Parameter(description = "Infos of the user to be created")
  @RequestBody @Valid UserDTO userDTO) {
    return userMapper.toUserDTO(userService.createUser(userMapper.toUserEntity(userDTO)));
  }

  @GetMapping(produces = "application/json", path = "/{id}")
  @Operation(summary = "Returns a specific user by its identifier.")
  @ApiResponses({ //
      @ApiResponse(responseCode = "200", description = "User with the given id was found",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserDTO.class))}),
      @ApiResponse(responseCode = "404", description = "No user found", content = @Content)
  })
  @Cacheable(value = "Users", key = "#id")
  public UserDTO readOneUser(
      @Parameter(description = "id of user to be searched") @PathVariable final Integer id) {
    System.out.println("In read");
    return userMapper.toUserDTO(userService.readOneUser(id));
  }

  @GetMapping(produces = "application/json", path = "")
  @Operation(summary = "Returns a Page of users.")
  @ApiResponses({ //
      @ApiResponse(responseCode = "200", description = "OK",
          content = {@Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))
          }),
      @ApiResponse(responseCode = "204", description = "Such User entries not found",
          content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Parameter are not valid! Notice: size > 1 and start >= 0",
          content = @Content)})
  public Page<UserDTO> readUsers(
      @Parameter(description = "index of the page to be shown")
      @RequestParam(name = "pageStart", required = true) int pageStart,
      @Parameter(description = "maximal number of users on a Page")
      @RequestParam(name = "pageSize", required = true) int pageSize) {

    return userService.readUsers(pageStart, pageSize).map(user -> userMapper.toUserDTO(user));
  }

}

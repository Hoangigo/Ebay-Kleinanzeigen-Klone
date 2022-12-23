package de.hs.da.hskleinanzeigen.controller;

import de.hs.da.hskleinanzeigen.dto.NotepadGetDTO;
import de.hs.da.hskleinanzeigen.dto.NotepadPutDTO;
import de.hs.da.hskleinanzeigen.mapper.NotepadMapper;
import de.hs.da.hskleinanzeigen.service.NotepadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

@RestController
@Tag(name = "Notepad", description = "Read, set and update Notepad, also delete entry from Notepad")
public class NotepadController {

  private final NotepadService notepadService;
  private final NotepadMapper notepadMapper;

  @Autowired
  public NotepadController(NotepadService notepadService, NotepadMapper notepadMapper) {
    this.notepadService = notepadService;
    this.notepadMapper = notepadMapper;
  }

  @PutMapping(consumes = "application/json", path = "/api/users/{userId}/notepad")
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(summary = "Add an AD to an User Notepad")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Notepad of the given user has successfully been updated",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = NotepadPutDTO.class))}),
      @ApiResponse(responseCode = "400", content = @Content,
          description = "Advertisement or User with the given ids not found OR payload incomplete")})
  public NotepadPutDTO addADtoNotepad(@Valid @RequestBody NotepadPutDTO note,
      @Parameter(description = "id of user whom notepad should be updated")
      @PathVariable Integer userId) {
    return notepadMapper.toNotepadPutDTO(notepadService.addADtoNotepad(note, userId));
  }

  @GetMapping(path = "/api/users/{userId}/notepad", produces = "application/json")
  @Operation(summary = "Get Notepad of a given user")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Return the whole Notepad of the given user",
          content = {@Content(mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = NotepadGetDTO.class)))}),
      @ApiResponse(responseCode = "404",
          description = "User with the given id not found", content = @Content),
      @ApiResponse(responseCode = "204", content = @Content,
          description = "No entry found on notepad for the given user")})
  public List<NotepadGetDTO> getNotepadByUser(@Parameter(description = "id of user")
  @PathVariable Integer userId) {

    return notepadService.getNotepadByUser(userId).stream()
        .map(notepad -> notepadMapper.toNotepadGetDTO(notepad)).collect(Collectors.toList());

  }


  @DeleteMapping(path = "/api/users/{userId}/notepad/{adId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  //wirklich so gewollt? JA, denn man würde bei 200 ein Body erwarten....
  @Operation(summary = "Delete an AD from Notepad of a given user")
  @ApiResponses({
      @ApiResponse(responseCode = "204", content = @Content,
          description = "Ad has been successfully removed from Notepad"),
      @ApiResponse(responseCode = "404",
          description = "User or Advertisement with the given id not found", content = @Content)})
  public void deleteNotepad(
      @Parameter(description = "id of user") @PathVariable Integer userId,
      @Parameter(description = "id of the advertisement to be deleted") @PathVariable Integer adId) {

    notepadService.deleteNotepad(userId, adId);
  }

}

package de.hs.da.hskleinanzeigen.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hs.da.hskleinanzeigen.dto.NotepadPutDTO;
import de.hs.da.hskleinanzeigen.entities.Notepad;
import de.hs.da.hskleinanzeigen.exception.AdNotFoundException;
import de.hs.da.hskleinanzeigen.exception.UserNotFoundException;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapperImpl;
import de.hs.da.hskleinanzeigen.mapper.CategoryMapperImpl;
import de.hs.da.hskleinanzeigen.mapper.NotepadMapper;
import de.hs.da.hskleinanzeigen.mapper.NotepadMapperImpl;
import de.hs.da.hskleinanzeigen.mapper.UserMapperImpl;
import de.hs.da.hskleinanzeigen.service.NotepadService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(value = NotepadController.class)
@Import({NotepadMapperImpl.class, AdvertisementMapperImpl.class, CategoryMapperImpl.class,
    UserMapperImpl.class})
public class NotepadControllerIT {

  @Autowired
  MockMvc mvc;

  @MockBean
  NotepadService service;

  @Autowired
  NotepadMapper mapper;

  @Autowired
  ObjectMapper objectMapper;

  @WithMockUser(username = "user", password = "user")
  @Test
  void deleteNotepad_whenAllOK() throws Exception {
    doNothing().when(service).deleteNotepad(Mockito.anyInt(), Mockito.anyInt());

    mvc.perform(delete("/api/users/{userId}/notepad/{adId}", 1, 2)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNoContent());
  }

  @WithMockUser(username = "user", password = "user")
  @Test
  void deleteNotepad_whenUserNotFound() throws Exception {
    doThrow(new UserNotFoundException()).when(service)
        .deleteNotepad(Mockito.anyInt(), Mockito.anyInt());

    mvc.perform(delete("/api/users/{userId}/notepad/{adId}", 1, 2)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof UserNotFoundException))
        .andExpect(result -> assertEquals(UserNotFoundException.outputMessage,
            result.getResolvedException().getMessage()));
  }

  @WithMockUser(username = "user", password = "user")
  @Test
  void getNotepadByUser_whenUserIdNotMatches() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(service)
        .getNotepadByUser(Mockito.anyInt());
    mvc.perform(get("/api/users/{userId}/notepad", Mockito.anyInt())
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            ResponseStatusException.class));
  }

  @WithMockUser(username = "user", password = "user")
  @Test
  void getNotepadByUser_whenAllOk() throws Exception {
    List<Notepad> list = Mockito.mock(List.class);
    when(service.getNotepadByUser(Mockito.anyInt())).thenReturn(list);
    mvc.perform(get("/api/users/{userId}/notepad", Mockito.anyInt())
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
    ;
  }


  @WithMockUser(username = "user", password = "user")
  @Test
  void addADtoNotepad_whenAdNotFound() throws Exception {
    when(service.addADtoNotepad(Mockito.any(), Mockito.anyInt())).thenThrow(
        new AdNotFoundException());
    NotepadPutDTO not = new NotepadPutDTO();
    not.setNote("we");
    not.setId(12);
    not.setAdvertisementId(23);
    not.setUserId(78);
    final String content = objectMapper.writeValueAsString(not);

    mvc.perform(put("/api/users/{userId}/notepad", 74)
            .with(csrf())
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof AdNotFoundException))
        .andExpect(result -> assertEquals(AdNotFoundException.outputMessage,
            result.getResolvedException().getMessage()));

  }

  @WithMockUser(username = "user", password = "user")
  @Test
  void addADtoNotepad_whenAllOK() throws Exception {
    Notepad notepad = Mockito.mock(Notepad.class);

    when(service.addADtoNotepad(Mockito.any(), Mockito.anyInt())).thenReturn(notepad);
    NotepadPutDTO not = new NotepadPutDTO();
    not.setNote("we");
    not.setId(12);
    not.setAdvertisementId(23);
    not.setUserId(78);
    final String content = objectMapper.writeValueAsString(not);

    mvc.perform(put("/api/users/{userId}/notepad", 74)
            .with(csrf())
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk());

  }
}

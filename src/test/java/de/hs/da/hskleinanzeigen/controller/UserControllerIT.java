package de.hs.da.hskleinanzeigen.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.mapper.UserMapper;
import de.hs.da.hskleinanzeigen.mapper.UserMapperImpl;
import de.hs.da.hskleinanzeigen.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(value = UserController.class)
@Import(UserMapperImpl.class)
public class UserControllerIT {

  final String BASE_PATH = "/api/users";
  @Autowired
  MockMvc mvc;

  @MockBean
  UserService service;

  @Autowired
  UserMapper userMapper;

  @Autowired
  ObjectMapper objectMapper;

  User generateUser() {
    return new User(110, "tch2@h-da.de", "geheim7", "Tch",
        "Sobadjo", "123456", "Etoudi",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  @WithMockUser(username = "nein", password = "user")
  @Test
  void readOneUser_whenIdMatches() throws Exception {
    //final String content = objectMapper.writeValueAsString(Mockito.anyInt());

    when(service.readOneUser(Mockito.anyInt())).thenReturn(generateUser());

    mvc.perform(get(BASE_PATH + "/{id}", Mockito.anyInt()).with(csrf())//.with(rob())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(result -> assertThat(
            objectMapper.readValue(result.getResponse().getContentAsString(),
                User.class)).isEqualTo(generateUser()));
  }

  @WithMockUser(username = "nein", password = "user")
  @Test
  void readOneUser_whenUserNotFound() throws Exception {
    //final String content = objectMapper.writeValueAsString(Mockito.anyInt());

    when(service.readOneUser(Mockito.anyInt())).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(get(BASE_PATH + "/{id}", Mockito.anyInt()).with(csrf())//.with(rob())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound())
    ;
  }

  @WithMockUser(username = "nein", password = "user")
  @Test
  void readUsers_whenAllOk() throws Exception {
    //final String content = objectMapper.writeValueAsString(Mockito.anyInt());
    Page<User> expected = Mockito.mock(Page.class);
    when(service.readUsers(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expected);

    mvc.perform(get(BASE_PATH).with(csrf())//.with(rob())
            .param("pageSize", String.valueOf(0))
            .param("pageStart", String.valueOf(5))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk());
  }

  @WithMockUser(username = "nein", password = "user")
  @Test
  void createUser_whenAllOk() throws Exception {

    when(service.createUser(Mockito.any())).thenReturn(null);

    mvc.perform(post(BASE_PATH).with(csrf())//.with(rob())
            .content(payloadUserDTO())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isCreated())
    ;
  }

  String payloadUserDTO() {
    String json = "{\n";
    json += "    \"email\": \"" + "email@email.de" + "\",\n";

    json += "    \"password\": \"" + "geheim" + "\",\n";

    json += "    \"firstName\": \"" + "me" + "\",\n";

    json += "    \"lastName\": \"" + "du" + "\",\n";

    json += "    \"phone\": \"" + "1458" + "\",\n" +
        "    \"location\": \"" + "sena" + "\"\n" +
        "}";
    return json;
  }
}

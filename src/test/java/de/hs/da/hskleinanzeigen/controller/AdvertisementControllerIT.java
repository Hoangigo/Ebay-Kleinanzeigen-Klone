package de.hs.da.hskleinanzeigen.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hs.da.hskleinanzeigen.dto.AdvertisementDTO;
import de.hs.da.hskleinanzeigen.dto.CategoryDTO;
import de.hs.da.hskleinanzeigen.dto.UserDTO;
import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Advertisement.AD_TYPE;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapper;
import de.hs.da.hskleinanzeigen.mapper.AdvertisementMapperImpl;
import de.hs.da.hskleinanzeigen.mapper.CategoryMapperImpl;
import de.hs.da.hskleinanzeigen.mapper.UserMapperImpl;
import de.hs.da.hskleinanzeigen.service.AdvertisementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = AdvertisementController.class)
@Import({AdvertisementMapperImpl.class, CategoryMapperImpl.class,
    UserMapperImpl.class})
public class AdvertisementControllerIT {

  private final String BASE_PATH = "/api/advertisements";
  @Autowired
  MockMvc mvc;

  @MockBean
  AdvertisementService service;

  @Autowired
  AdvertisementMapper mapper;

  @Autowired
  ObjectMapper objectMapper;

  @WithMockUser(username = "nein", password = "user")
  @Test
  void readOneAdvertisement_whenAllOk() throws Exception {
    Advertisement ad = Mockito.mock(Advertisement.class);
    when(service.readOneAdvertisement(Mockito.anyInt())).thenReturn(ad);
    mvc.perform(get(BASE_PATH + "/{id}", Mockito.anyInt())
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
    ;
  }

  @WithMockUser(username = "nein", password = "user")
  @Test
  void readAdvertisements_whenAllOk() throws Exception {
    Page<Advertisement> expected = Mockito.mock(Page.class);
    when(service.readAdvertisements(AD_TYPE.OFFER, 1, 2,
        3, 0, 5)).thenReturn(expected);
    mvc.perform(get(BASE_PATH)
            .with(csrf())
            .param("pageSize", String.valueOf(5))
            .param("type", String.valueOf(AD_TYPE.OFFER))
            .param("category", String.valueOf(1))
            .param("priceFrom", String.valueOf(2))
            .param("priceTo", String.valueOf(3))
            .param("pageStart", String.valueOf(0))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
    ;
  }

  @WithMockUser(username = "nein", password = "user")
  @Test
  void createAdvertisement_whenAllOk() throws Exception {
    AdvertisementDTO newAd = new AdvertisementDTO(1, AD_TYPE.OFFER, new CategoryDTO(),
        new UserDTO(),
        "title", "desc", 12, "douala");
    String content = objectMapper.writeValueAsString(newAd);
    Advertisement ad = Mockito.mock(Advertisement.class);
    when(service.createAdvertisement(Mockito.any())).thenReturn(null);
    mvc.perform(post(BASE_PATH)
            .with(csrf())
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isCreated())
    ;
  }
}

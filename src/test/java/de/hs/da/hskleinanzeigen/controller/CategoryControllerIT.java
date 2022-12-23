package de.hs.da.hskleinanzeigen.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.exception.CategoryNameAlreadyExitsException;
import de.hs.da.hskleinanzeigen.mapper.CategoryMapper;
import de.hs.da.hskleinanzeigen.mapper.CategoryMapperImpl;
import de.hs.da.hskleinanzeigen.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = CategoryController.class)
@Import(CategoryMapperImpl.class)
//@AutoConfigureMockMvc
public class CategoryControllerIT {

  @Autowired
  MockMvc mvc;

  @MockBean
  CategoryService service;

  @Autowired
  CategoryMapper categoryMapper;

  @Autowired
  ObjectMapper objectMapper;

  final String BASE_PATH = "/api/categories";

  private static final String CATEGORY_PAYLOAD_INCOMPLETE = "{\n" +
      "   \"parentId\": 4711,\n" +
      "}\n";

  Category generateCategory() {
    Category cat = new Category();
    cat.setName("Tch");
    cat.setId(1);
    //cat.setParent_id(2);

    return cat;
  }

  /*@BeforeEach
  void setUp()
  {}*/

  /*public static RequestPostProcessor rob() {
    return user("adin").password("a").roles("Aministrator");
  }*/

  @WithMockUser(username = "admin", password = "admin")
  //TODO wieso muss es nicht zu WebSecurity passen?
  @Test
  void createCategory_whenSucessfull() throws Exception {
    final String content = objectMapper.writeValueAsString(
        categoryMapper.toCategoryDTO(generateCategory()));
    when(service.createCategory(generateCategory())).thenReturn(generateCategory());
    mvc.perform(post(BASE_PATH).with(csrf())//.with(rob())
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)).andExpect(status().isCreated()).andDo(print())
        .andExpect(result -> assertThat(
            objectMapper.readValue(result.getResponse().getContentAsString(),
                Category.class)).isEqualTo(generateCategory()));
  }

  @WithMockUser(username = "test", password = "user")
  @Test
  void createCategory_whenNameAlreadyExits() throws Exception {
    //final String content = objectMapper.writeValueAsString(CATEGORY_PAYLOAD_INCOMPLETE);
    final String content = objectMapper.writeValueAsString(
        categoryMapper.toCategoryDTO(generateCategory()));
    doThrow(new CategoryNameAlreadyExitsException()).when(service)
        .createCategory(Mockito.any());
    mvc.perform(post(BASE_PATH).with(csrf())//.with(rob())
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)).andExpect(status().isConflict())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(
            CategoryNameAlreadyExitsException.class));
  }
}

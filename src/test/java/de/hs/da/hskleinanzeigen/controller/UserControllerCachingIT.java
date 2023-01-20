package de.hs.da.hskleinanzeigen.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.service.UserService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

//@WebMvcTest(value = UserController.class)
//@Import(UserMapperImpl.class)
@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerCachingIT {

  final String BASE_PATH = "/api/users";

  @Autowired
  MockMvc mvc;


  @MockBean
  UserService service;

  @Autowired
  CacheManager cacheManager;

  @Autowired
  ObjectMapper objectMapper;

  private static GenericContainer genericContainer;

  User generateUser(int id, String text) {
    return new User(id, (text + "@h-da.de"), "geheim", "Tch",
        "Sobadjo", "123456", "Etoudi",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  @BeforeAll
  public static void setupRedisContainer() {
    genericContainer = new GenericContainer(
        DockerImageName.parse("redis:5.0.3-alpine")
    ).withExposedPorts(6379);
    genericContainer.start();
    System.setProperty("spring.redis.host", genericContainer.getHost());
    System.setProperty("spring.redis.port", genericContainer.getMappedPort(6379).toString());
  }

  @AfterAll
  public static void teardownRedisServer() {
    genericContainer.stop();
  }





  @Test
  @WithMockUser(username = "admin", password = "admin")
  void readOneUser_FromCacheAfterBeenCreated()  throws Exception{
    User returnedUser = generateUser(1089, "readAfterCreate");

    when(service.createUser(Mockito.any())).thenReturn(returnedUser);

    /*Mockito.when(repository.findByEmail(Mockito.any()))
        .thenReturn(Optional.ofNullable(null));
    Mockito.when(repository.save(Mockito.any()))
        .thenReturn(returnedUser);*/

    mvc.perform(post(BASE_PATH)//.with(rob())
            .content(payloadUserDTO())//.with(user("admin").password("admin"))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isCreated())
    ;

    //assertThat(returnedUser).isEqualTo(userService.createUser(returnedUser));

    //check if the created user object was stored in Cache
    Cache userCache = cacheManager.getCache("Users");
    assertThat(userCache.get(returnedUser.getId(), User.class)).isNotNull();

    //Because of CachePUT this should return the User from the cache without invoking the repository
   // User userCacheHit = userService.readOneUser(returnedUser.getId());
    mvc.perform(get(BASE_PATH + "/{id}", returnedUser.getId()).with(csrf())//.with(rob())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(result -> AssertionsForClassTypes.assertThat(
            objectMapper.readValue(result.getResponse().getContentAsString(),
                User.class)).isEqualTo(returnedUser));
    //assertThat(returnedUser).isEqualTo(userCacheHit);
    Mockito.verify(service, times(0)).readOneUser(returnedUser.getId());

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

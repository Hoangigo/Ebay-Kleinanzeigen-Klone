package de.hs.da.hskleinanzeigen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Import({UserService.class})
@ExtendWith(SpringExtension.class)
@EnableCaching
@ImportAutoConfiguration(classes = {
    CacheAutoConfiguration.class,
    RedisAutoConfiguration.class
})

public class UserServiceCachingIntegrationTest {

  @MockBean
  private UserRepository repository;

  @Autowired
  CacheManager cacheManager;

  @Autowired
  private UserService userService;

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

  /*@BeforeEach
  void setUp() {
    userService = new UserService(repository);
  }*/

  @Test
  void readOneUser_FromCacheAfterBeenCreated() {
    User returnedUser = generateUser(1089, "readAfterCreate");

    Mockito.when(repository.findByEmail(Mockito.any()))
        .thenReturn(Optional.ofNullable(null));
    Mockito.when(repository.save(Mockito.any()))
        .thenReturn(returnedUser);

    assertThat(returnedUser).isEqualTo(userService.createUser(returnedUser));

    //check if the created user object was stored in Cache
    Cache userCache = cacheManager.getCache("Users");
    assertThat(userCache.get(returnedUser.getId(), User.class)).isNotNull();

    //Because of CachePUT this should return the User from the cache without invoking the repository
    User userCacheHit = userService.readOneUser(returnedUser.getId());
    assertThat(returnedUser).isEqualTo(userCacheHit);
    Mockito.verify(repository, times(0)).findById(returnedUser.getId());

  }

  @Test
  void readOneUser_whenSameUserBeReadTwice() {
    User existingUser = generateUser(1028, "readTwice");

    Mockito.when(repository.findById(existingUser.getId()))
        .thenReturn(Optional.of(existingUser));

    //check if the user object is not stored in cache yet
    Cache userCache = cacheManager.getCache("Users");
    assertThat(userCache.get(existingUser.getId(), User.class)).isNull();

    User userCacheMiss = userService.readOneUser(existingUser.getId());
    assertThat(userCacheMiss).isEqualTo(existingUser);

    //check if the read user object was stored in Cache
    Cache userCache2 = cacheManager.getCache("Users");
    assertThat(userCache2.get(existingUser.getId(), User.class)).isNotNull();

    //check of the next invocation this user would be answered from cache
    //the second invocation should return the item from the cache without invoking the repository
    User userCacheHit = userService.readOneUser(existingUser.getId());
    assertThat(userCacheHit).isEqualTo(existingUser);
    Mockito.verify(repository, times(1)).findById(existingUser.getId());
  }

}

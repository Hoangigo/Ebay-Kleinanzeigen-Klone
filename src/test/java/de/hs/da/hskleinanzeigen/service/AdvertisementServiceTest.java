package de.hs.da.hskleinanzeigen.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Advertisement.AD_TYPE;
import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

  UserServiceTest usertest = new UserServiceTest();

  Advertisement generateAd() {
    return new Advertisement(1, AD_TYPE.OFFER, new Category(7, 8, "tch"),
        usertest.generateUser(), "title", "description", 10, "location",
        new java.sql.Timestamp(System.currentTimeMillis()));
  }

  final int validPageSize = 10;
  final int invalidPageSize = -5;
  final int validPageStart = 1;
  final int invalidPageStart = -2;

  /*AD_TYPE type = AD_TYPE.REQUEST;
  final Integer category = 2;
  final Integer priceFrom, final Integer priceTo*/

  private AdvertisementService service;

  @Mock
  private AdvertisementRepository adRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CategoryRepository catRepository;

  @BeforeEach
  void setUp() {
    service = new AdvertisementService(adRepository, catRepository, userRepository);
  }

  @Test
  void readOneAdvertisement_whenAdNotExits() {
    Mockito.when(adRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.readOneAdvertisement(Mockito.anyInt()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(exception.getReason()).isEqualTo("Advertisement with this id not found");

    Mockito.verify(adRepository).findById(Mockito.anyInt());
  }

  @Test
  void readOneAdvertisement_whenIdMatch() {
    Advertisement ad = Mockito.mock(Advertisement.class);
    Mockito.when(adRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(ad));

    Advertisement result = service.readOneAdvertisement(Mockito.anyInt());

    assertThat(result).isSameAs(ad);

    Mockito.verify(adRepository).findById(Mockito.anyInt());
  }

  @Test
  void readAdvertisements_whenPageParameterInvalid() {
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.readAdvertisements(AD_TYPE.OFFER, 1, 1,
            1, invalidPageStart, validPageSize));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

    exception = assertThrows(ResponseStatusException.class,
        () -> service.readAdvertisements(AD_TYPE.OFFER, 1, 2,
            3, validPageStart, invalidPageSize));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

    exception = assertThrows(ResponseStatusException.class,
        () -> service.readAdvertisements(AD_TYPE.REQUEST, 1, 110,
            5, invalidPageStart, invalidPageSize));
    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void readAdvertisements_whenNoContent() {
    Page<Advertisement> emptyPage = Page.empty();
    Mockito.when(
        adRepository.findAdvertisements((Pageable) Mockito.any(), Mockito.any(), Mockito.anyInt(),
            Mockito.anyInt(), Mockito.anyInt())).thenReturn(emptyPage);
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.readAdvertisements(AD_TYPE.OFFER, 1, 2,
            3, validPageStart, validPageSize));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void readAdvertisements_whenOKWithContent() {
    Page<Advertisement> expected = Mockito.mock(Page.class);

    Mockito.when(
        adRepository.findAdvertisements(Mockito.any(), Mockito.any(), Mockito.anyInt(),
            Mockito.anyInt(), Mockito.anyInt())).thenReturn(expected);

    Page<Advertisement> result = service.readAdvertisements(AD_TYPE.OFFER, 1, 2,
        3, validPageStart, validPageSize);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void createAdvertisement_whenCategoryNotExits() {
    Mockito.when(catRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.createAdvertisement(generateAd()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(exception.getReason()).isEqualTo(
        "Category with the given id not found, so we can create a new Advertisement");

    Mockito.verify(catRepository).findById(Mockito.anyInt());
  }

  @Test
  void createAdvertisement_whenUserNotExits() {
    Category cat = Mockito.mock(Category.class);
    Mockito.when(catRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(cat));
    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.createAdvertisement(generateAd()));

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(exception.getReason()).isEqualTo(
        "User with the given id not found, so we can create a new Advertisement");

    Mockito.verify(catRepository).findById(Mockito.anyInt());
    Mockito.verify(userRepository).findById(Mockito.anyInt());
  }

  @Test
  void createAdvertisement_whenOK() {
    Category cat = Mockito.mock(Category.class);
    User user = Mockito.mock(User.class);
    Advertisement newAd = generateAd();
    Mockito.when(catRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(cat));
    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(user));
    Mockito.when(adRepository.save(Mockito.any())).thenReturn(newAd);

    Advertisement result = service.createAdvertisement(newAd);

    assertThat(result).isSameAs(newAd);

    Mockito.verify(catRepository).findById(Mockito.anyInt());
    Mockito.verify(userRepository).findById(Mockito.anyInt());
  }
}

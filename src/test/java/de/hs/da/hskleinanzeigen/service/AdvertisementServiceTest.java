package de.hs.da.hskleinanzeigen.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Advertisement.AD_TYPE;
import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.exception.AdNotFoundException;
import de.hs.da.hskleinanzeigen.exception.CategoryNotFoundException;
import de.hs.da.hskleinanzeigen.exception.NoContentException;
import de.hs.da.hskleinanzeigen.exception.PayloadIncorrectException;
import de.hs.da.hskleinanzeigen.exception.UserNotFoundException;
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

    Exception exception = assertThrows(AdNotFoundException.class,
        () -> service.readOneAdvertisement(Mockito.anyInt()));

    assertThat(exception.getMessage()).isEqualTo(AdNotFoundException.outputMessage);

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
    assertThrows(PayloadIncorrectException.class,
        () -> service.readAdvertisements(AD_TYPE.OFFER, 1, 1,
            1, invalidPageStart, validPageSize));

    assertThrows(PayloadIncorrectException.class,
        () -> service.readAdvertisements(AD_TYPE.OFFER, 1, 2,
            3, validPageStart, invalidPageSize));

    assertThrows(PayloadIncorrectException.class,
        () -> service.readAdvertisements(AD_TYPE.REQUEST, 1, 110,
            5, invalidPageStart, invalidPageSize));
  }

  @Test
  void readAdvertisements_whenNoContent() {
    Page<Advertisement> emptyPage = Page.empty();
    Mockito.when(
        adRepository.findAdvertisements(Mockito.any(), Mockito.any(), Mockito.anyInt(),
            Mockito.anyInt(), Mockito.anyInt())).thenReturn(emptyPage);
    Exception exception = assertThrows(NoContentException.class,
        () -> service.readAdvertisements(AD_TYPE.OFFER, 1, 2,
            3, validPageStart, validPageSize));
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

    Exception exception = assertThrows(CategoryNotFoundException.class,
        () -> service.createAdvertisement(generateAd()));

    assertThat(exception.getMessage()).isEqualTo(
        CategoryNotFoundException.outputMessage);

    Mockito.verify(catRepository).findById(Mockito.anyInt());
  }

  @Test
  void createAdvertisement_whenUserNotExits() {
    Category cat = Mockito.mock(Category.class);
    Mockito.when(catRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(cat));
    Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));

    Exception exception = assertThrows(UserNotFoundException.class,
        () -> service.createAdvertisement(generateAd()));

    assertThat(exception.getMessage()).isEqualTo(
        UserNotFoundException.outputMessage);

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

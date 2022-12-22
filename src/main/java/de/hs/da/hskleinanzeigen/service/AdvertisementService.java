package de.hs.da.hskleinanzeigen.service;

import de.hs.da.hskleinanzeigen.entities.Advertisement;
import de.hs.da.hskleinanzeigen.entities.Advertisement.AD_TYPE;
import de.hs.da.hskleinanzeigen.entities.Category;
import de.hs.da.hskleinanzeigen.entities.User;
import de.hs.da.hskleinanzeigen.repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

  private final AdvertisementRepository advertisementRepository;

  private final CategoryRepository categoryRepository;

  private final UserRepository userRepository;

  public Advertisement readOneAdvertisement(final Integer id) throws ResponseStatusException {
    Optional<Advertisement> advertisement = advertisementRepository.findById(id);
    if (advertisement.isPresent()) {
      return advertisement.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advertisement with this id not found");
  }

  public Page<Advertisement> readAdvertisements(AD_TYPE type, final Integer category,
      final Integer priceFrom, final Integer priceTo, final Integer pageStart,
      final Integer pageSize) throws ResponseStatusException {

    if (pageSize < 1 || pageStart < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Parameter are not valid! Notice: size > 1 and start >= 0");
    }

    Pageable indexOfPageAndNumberOfElements = PageRequest.of(pageStart, pageSize,
        Sort.by("created").ascending());

    Page<Advertisement> result = advertisementRepository.findAdvertisements(
        indexOfPageAndNumberOfElements, type, category,
        priceFrom, priceTo);

    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NO_CONTENT,
          "Such Advertisement entries not found");
    }

    return result;
  }

  public Advertisement createAdvertisement(Advertisement advertisement)
      throws ResponseStatusException {
    Optional<Category> category = categoryRepository.findById(
        advertisement.getCategory().getId());
    if (category.isPresent()) {
      Optional<User> user = userRepository.findById(advertisement.getUser().getId());
      if (user.isPresent()) {
        advertisement.setUser(user.get());
        advertisement.setCategory(category.get());
        return advertisementRepository.save(advertisement);
      }
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "User with the given id not found, so we can create a new Advertisement");
    }

    //return advertisementRepository.findById(advertisement.getId()).get();
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Category with the given id not found, so we can create a new Advertisement");
  }
}

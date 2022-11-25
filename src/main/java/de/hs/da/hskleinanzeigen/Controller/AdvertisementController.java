package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.Entities.Advertisement;
import de.hs.da.hskleinanzeigen.Entities.Advertisement.AD_TYPE;
import de.hs.da.hskleinanzeigen.Entities.Category;
import de.hs.da.hskleinanzeigen.Entities.User;
import de.hs.da.hskleinanzeigen.Repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.Repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.Repository.UserRepository;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/advertisements")
public class AdvertisementController {

  private final AdvertisementRepository advertisementRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  @Autowired
  public AdvertisementController(AdvertisementRepository advertisementRepository,
      CategoryRepository categoryRepository, UserRepository userRepository) {
    this.advertisementRepository = advertisementRepository;
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }


  @GetMapping(path = "/{id}")
  public Advertisement readOneAdvertisement(@PathVariable Integer id) {
    Optional<Advertisement> advertisement = advertisementRepository.findById(id);
    if (advertisement.isPresent()) {
      return advertisement.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advertisement with this id not found");
  }

  @GetMapping(path = "")
  public Page<Advertisement> readAdvertisements(
      @RequestParam(name = "type", required = false) AD_TYPE type,
      @RequestParam(name = "category", required = false) Integer category,
      @RequestParam(name = "priceFrom", required = false) Integer priceFrom,
      @RequestParam(name = "priceTo", required = false) Integer priceTo,
      @RequestParam(name = "pageStart", required = true) Integer pageStart,
      @RequestParam(name = "pageSize", required = true) Integer pageSize) {

    if ((pageSize < 1) || (pageStart < 0)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Parameter are not valid! Notice: size > 1 and start >= 0");
    }

    Pageable indexOfPageAndNumberOfElements = PageRequest.of(pageStart, pageSize,
        Sort.by("created").ascending()); //TODO muss man hier sortieren?

    Page<Advertisement> result = advertisementRepository.findAdvertisements(
        indexOfPageAndNumberOfElements, type, category,
        priceFrom, priceTo);

    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Such User entries not found");
    }

    return result;
  }


  @PostMapping("")
  @ResponseStatus(code = HttpStatus.CREATED)
  public Advertisement createAdvertisement(@RequestBody @Valid Advertisement advertisement) {
    Optional<Category> category = categoryRepository.findById(advertisement.getCategory().getId());
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

  private Page<Advertisement> selectRightQuery(Pageable indexOfPageAndNumberOfElements,
      Optional<String> typ, Optional<Integer> categor, Optional<Integer> priceFro,
      Optional<Integer> priceToo) {

    if (typ.isPresent()) {//t
      String typeS = typ.get();
      AD_TYPE type = AD_TYPE.valueOf(typeS);
      if (categor.isPresent()) { //t c
        int category = categor.get();
        if (priceFro.isPresent()) { // t c pf
          int priceFrom = priceFro.get();
          if (priceToo.isPresent()) { //t c pf pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByTypeCategoryPriceToPriceFrom(
                indexOfPageAndNumberOfElements, type, category,
                priceTo, priceFrom);
          } else {
            //t c pf
            return advertisementRepository.findByTypeCategoryPriceFrom(
                indexOfPageAndNumberOfElements, type, category, priceFrom);
          }
        } else {
          if (priceToo.isPresent()) { //t c pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByTypeCategoryPriceTo(
                indexOfPageAndNumberOfElements, type, category,
                priceTo);
          } else {
            //t c
            return advertisementRepository.findByTypeCategory(
                indexOfPageAndNumberOfElements, type, category);
          }
        }
      } else { //t
        if (priceFro.isPresent()) {//t pf
          int priceFrom = priceFro.get();
          if (priceToo.isPresent()) { //t pf pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByTypePriceToPriceFrom(
                indexOfPageAndNumberOfElements, type,
                priceTo, priceFrom);
          } else {
            //t pf
            return advertisementRepository.findByTypePriceFrom(
                indexOfPageAndNumberOfElements, type, priceFrom);
          }
        } else { //t
          if (priceToo.isPresent()) {//t pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByTypePriceTo(
                indexOfPageAndNumberOfElements, type,
                priceTo);
          } else {
            //t
            return advertisementRepository.findByType(
                indexOfPageAndNumberOfElements, type);
          }
        }
      }
    } else {
      if (categor.isPresent()) { //c
        int category = categor.get();
        if (priceFro.isPresent()) { //c pf
          int priceFrom = priceFro.get();

          if (priceToo.isPresent()) { //c pf pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByCategoryPriceToPriceFrom(
                indexOfPageAndNumberOfElements, category,
                priceTo, priceFrom);
          } else {
            //c pf
            return advertisementRepository.findByCategoryPriceFrom(
                indexOfPageAndNumberOfElements, category, priceFrom);
          }
        } else {
          if (priceToo.isPresent()) { // c pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByCategoryPriceTo(
                indexOfPageAndNumberOfElements, category,
                priceTo);
          } else {
            //c
            return advertisementRepository.findByCategory(
                indexOfPageAndNumberOfElements, category);
          }
        }
      } else {
        if (priceFro.isPresent()) { // pf
          int priceFrom = priceFro.get();
          if (priceToo.isPresent()) {//pf pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByPriceToPriceFrom(
                indexOfPageAndNumberOfElements,
                priceTo, priceFrom);
          } else {
            //pf
            return advertisementRepository.findByPriceFrom(
                indexOfPageAndNumberOfElements, priceFrom);
          }
        } else {
          if (priceToo.isPresent()) {//pt
            int priceTo = priceToo.get();
            return advertisementRepository.findByPriceTo(
                indexOfPageAndNumberOfElements, priceTo);
          } else {
            //nichts
            return advertisementRepository.findAll(indexOfPageAndNumberOfElements);
          }
        }
      }
    }
  }


}

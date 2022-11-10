package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.Entities.Advertisement;
import de.hs.da.hskleinanzeigen.Entities.Category;
import de.hs.da.hskleinanzeigen.Repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.Repository.CategoryRepository;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/advertisements")
public class AdvertisementController {

  private final AdvertisementRepository advertisementRepository;
  private final CategoryRepository categoryRepository;

  @Autowired
  public AdvertisementController(AdvertisementRepository advertisementRepository,
      CategoryRepository categoryRepository) {
    this.advertisementRepository = advertisementRepository;
    this.categoryRepository = categoryRepository;
  }


  @GetMapping(path = "/{id}")
  public Advertisement readAdvertisement(@PathVariable Integer id) {
    Optional<Advertisement> advertisement = advertisementRepository.findById(id);
    if (advertisement.isPresent()) {
      return advertisement.get();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advertisement with this id not found");
  }

  @PostMapping("")
  @ResponseStatus(code = HttpStatus.CREATED)
  public Advertisement createAdvertisement(@Valid @RequestBody Advertisement advertisement) {
    Optional<Category> category = categoryRepository.findById(advertisement.getCategory().getId());
    if (category.isPresent()) {
      advertisement.setCategory(category.get());
      return advertisementRepository.save(advertisement);
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        "Category with the given id not found, so we can create a new Advertisement");
  }

}

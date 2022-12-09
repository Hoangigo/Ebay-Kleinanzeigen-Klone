package de.hs.da.hskleinanzeigen.Controller;

import de.hs.da.hskleinanzeigen.DTO.AdvertisementDTO;
import de.hs.da.hskleinanzeigen.Entities.Advertisement;
import de.hs.da.hskleinanzeigen.Entities.Advertisement.AD_TYPE;
import de.hs.da.hskleinanzeigen.Entities.Category;
import de.hs.da.hskleinanzeigen.Entities.User;
import de.hs.da.hskleinanzeigen.Mapper.AdvertisementMapper;
import de.hs.da.hskleinanzeigen.Mapper.CategoryMapper;
import de.hs.da.hskleinanzeigen.Mapper.UserMapper;
import de.hs.da.hskleinanzeigen.Repository.AdvertisementRepository;
import de.hs.da.hskleinanzeigen.Repository.CategoryRepository;
import de.hs.da.hskleinanzeigen.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Advertisement", description = "Read, set, update and delete Advertisements and their properties")
public class AdvertisementController {

  private final AdvertisementRepository advertisementRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  private final AdvertisementMapper adMapper;

  private final UserMapper userMapper;

  private final CategoryMapper categoryMapper;

  @Autowired
  public AdvertisementController(AdvertisementRepository advertisementRepository,
      CategoryRepository categoryRepository, UserRepository userRepository,
      AdvertisementMapper adMapper, UserMapper userMapper, CategoryMapper categoryMapper) {
    this.advertisementRepository = advertisementRepository;
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
    this.adMapper = adMapper;
    this.userMapper = userMapper;
    this.categoryMapper = categoryMapper;
  }


  @GetMapping(produces = "application/json", path = "/{id}")
  @Operation(summary = "Get Advertisement by its id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Return the searched Advertisement",
          content = { @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AdvertisementDTO.class))}),
      @ApiResponse(responseCode = "404", content = @Content,
          description = "Advertisement with this id not found")})
  public AdvertisementDTO readOneAdvertisement(
      @Parameter(description = "id of the AD to be searched for") @PathVariable Integer id) {
    Optional<Advertisement> advertisement = advertisementRepository.findById(id);
    if (advertisement.isPresent()) {
      return adMapper.toADDTO(advertisement.get());
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advertisement with this id not found");
  }

  @GetMapping(produces = "application/json")
  @Operation(summary = "Get a Page of Advertisements")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Return Page of Advertisements",
          content = { @Content(mediaType = "application/json",
              schema = @Schema(implementation = AdvertisementDTO.class))}),
      @ApiResponse(responseCode = "204", content = @Content,
          description = "Such Advertisement entries not found"),
      @ApiResponse(responseCode = "400", content = @Content,
          description = "Parameter are not valid! Notice: size > 1 and start >= 0")})
  public Page<AdvertisementDTO> readAdvertisements(
      @Parameter(description = "Type of Advertisement, you searched for")
      @RequestParam(name = "type", required = false) AD_TYPE type,
      @Parameter(description = "Category of Advertisement, you searched for")
      @RequestParam(name = "category", required = false) Integer category,
      @Parameter(description = "Min. price of Advertisement, you searched for")
      @RequestParam(name = "priceFrom", required = false) Integer priceFrom,
      @Parameter(description = "Max. price of Advertisement, you searched for")
      @RequestParam(name = "priceTo", required = false) Integer priceTo,
      @Parameter(description = "index of the page to be shown")
      @RequestParam(name = "pageStart", required = true) Integer pageStart,
      @Parameter(description = "maximal number of advertisements on a page")
      @RequestParam(name = "pageSize", required = true) Integer pageSize) {

    if ((pageSize < 1) || (pageStart < 0)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Parameter are not valid! Notice: size > 1 and start >= 0");
    }

    Pageable indexOfPageAndNumberOfElements = PageRequest.of(pageStart, pageSize,
        Sort.by("created").ascending());

    Page<Advertisement> result = advertisementRepository.findAdvertisements(
        indexOfPageAndNumberOfElements, type, category,
        priceFrom, priceTo);

    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Such Advertisement entries not found");
    }

    return result.map(advertisement -> adMapper.toADDTO(advertisement));
  }


  @PostMapping(consumes = "application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @Operation(summary = "Create a new Advertisement")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "New Advertisement has been created",
          content = { @Content(mediaType = "application/json",
              schema = @Schema(implementation = AdvertisementDTO.class))}),
      @ApiResponse(responseCode = "204", content = @Content,
          description = "Such Advertisement entries not found"),
      @ApiResponse(responseCode = "400", content = @Content,
          description = "User or Category with the given id not found OR payload incomplete")})
  public AdvertisementDTO createAdvertisement(@RequestBody @Valid AdvertisementDTO advertisementDTO) {
    Optional<Category> category = categoryRepository.findById(advertisementDTO.getCategory().getId());
    if (category.isPresent()) {
      Optional<User> user = userRepository.findById(advertisementDTO.getUser().getId());
      if (user.isPresent()) {
        advertisementDTO.setUser(userMapper.toUserDTO(user.get()));
        advertisementDTO.setCategory(categoryMapper.toCategoryDTO(category.get()));
        return adMapper.toADDTO(advertisementRepository.save(adMapper.toADEntity(advertisementDTO)));
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

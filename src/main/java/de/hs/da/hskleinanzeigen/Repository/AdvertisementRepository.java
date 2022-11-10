package de.hs.da.hskleinanzeigen.Repository;

import de.hs.da.hskleinanzeigen.Entities.Advertisement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends CrudRepository<Advertisement, Integer> {

}

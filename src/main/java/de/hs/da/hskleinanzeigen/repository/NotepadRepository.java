package de.hs.da.hskleinanzeigen.repository;

import de.hs.da.hskleinanzeigen.entities.Notepad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotepadRepository extends CrudRepository<Notepad, Integer> {

  Optional<Notepad> findByUserIdAndAdId(@Param("userId") int userId, @Param("adId") int adId);

  List<Notepad> findByUserId(Integer user_id);

  @Transactional
  @Modifying
  void deleteByUserIdAndAdId(@Param("userId") int userId, @Param("adId") int adId);
}

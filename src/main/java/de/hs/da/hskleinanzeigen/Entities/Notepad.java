package de.hs.da.hskleinanzeigen.Entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NOTEPAD")
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NamedQuery(name = "Notepad.findByUserIdAndAdId",
query = "SELECT n FROM Notepad n WHERE n.advertisement.id = :adId AND n.user.id = :userId")
@NamedQuery(name = "Notepad.deleteByUserIdAndAdId",
query = "DELETE FROM Notepad n WHERE n.user.id = :userId AND n.advertisement.id = :adId")

public class Notepad {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "ad_id", nullable = false)
  private Advertisement advertisement;

  private String note;

  @Column(nullable = false)
  private Timestamp created;
}

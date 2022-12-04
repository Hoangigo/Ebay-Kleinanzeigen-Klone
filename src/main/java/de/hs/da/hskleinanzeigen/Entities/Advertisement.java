package de.hs.da.hskleinanzeigen.Entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "AD")

@NamedQuery(name = "Advertisement.findByTypeCategoryPriceToPriceFrom",
    query = "SELECT a FROM Advertisement a where a.type = :t AND a.category.id = :c "
        + "AND a.price >= :pf AND a.price <= :pt")
@NamedQuery(name = "Advertisement.findByTypeCategoryPriceTo",//
    query = "SELECT a FROM Advertisement a where a.type = :t AND a.category.id = :c "
        + " AND a.price <= :pt")
@NamedQuery(name = "Advertisement.findByTypeCategoryPriceFrom",//
    query = "SELECT a FROM Advertisement a where a.type = :t AND a.category.id = :c "
        + "AND a.price >= :pf ")
@NamedQuery(name = "Advertisement.findByTypeCategory",//
    query = "SELECT a FROM Advertisement a where a.type = :t AND a.category.id = :c ")
@NamedQuery(name = "Advertisement.findByTypePriceToPriceFrom",//
    query = "SELECT a FROM Advertisement a where a.type = :t  "
        + "AND a.price >= :pf AND a.price <= :pt")
@NamedQuery(name = "Advertisement.findByTypePriceTo",//
    query = "SELECT a FROM Advertisement a where a.type = :t  "
        + " AND a.price <= :pt")
@NamedQuery(name = "Advertisement.findByTypePriceFrom",//
    query = "SELECT a FROM Advertisement a where a.type = :t  "
        + "AND a.price >= :pf ")
@NamedQuery(name = "Advertisement.findByType",//
    query = "SELECT a FROM Advertisement a where a.type = :t ")
@NamedQuery(name = "Advertisement.findByCategoryPriceToPriceFrom",//
    query = "SELECT a FROM Advertisement a where  a.category.id = :c "
        + "AND a.price >= :pf AND a.price <= :pt")
@NamedQuery(name = "Advertisement.findByCategoryPriceTo",//
    query = "SELECT a FROM Advertisement a where  a.category.id = :c "
        + " AND a.price <= :pt")
@NamedQuery(name = "Advertisement.findByCategoryPriceFrom",//
    query = "SELECT a FROM Advertisement a where  a.category.id = :c "
        + "AND a.price >= :pf ")
@NamedQuery(name = "Advertisement.findByCategory",//
    query = "SELECT a FROM Advertisement a where  a.category.id = :c ")
@NamedQuery(name = "Advertisement.findByPriceToPriceFrom",//
    query = "SELECT a FROM Advertisement a where  "
        + " a.price >= :pf AND a.price <= :pt")
@NamedQuery(name = "Advertisement.findByPriceTo",//
    query = "SELECT a FROM Advertisement a where  "
        + "  a.price <= :pt")
@NamedQuery(name = "Advertisement.findByPriceFrom",//
    query = "SELECT a FROM Advertisement a where  "
        + " a.price >= :pf ")

@NamedQuery(name = "Advertisement.findAdvertisements",
query = "SELECT a FROM Advertisement a "
    + "WHERE (:t is null OR a.type = :t) AND "
    + "(:c is null OR a.category.id = :c ) AND "
    + "(:pt is null  OR a.price <= :pt) AND "
    + "(:pf is null OR a.price >= :pf)")

@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Advertisement {

  public enum AD_TYPE {
    OFFER,
    REQUEST
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  private Integer id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AD_TYPE type;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  private Integer price;

  private String location;

  @Column(nullable = false)
  private Timestamp created;

}

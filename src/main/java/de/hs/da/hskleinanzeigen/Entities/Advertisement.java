package de.hs.da.hskleinanzeigen.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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


public class Advertisement {

  public Advertisement() {

  }

  public enum AD_TYPE {
    OFFER,
    REQUEST
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @NotNull(message = "Payload incomplete, Type ist mandatory") //Post request returns 400 when type not there
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AD_TYPE type;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  @NotNull(message = "Payload incomplete, Category ist mandatory")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull
  private User user;

  @Column(nullable = false)
  @NotEmpty(message = "Payload incomplete, Title ist mandatory")
  private String title;

  @Column(nullable = false)
  @NotEmpty(message = "Payload incomplete, Description ist mandatory")
  private String description;

  private Integer price;

  private String location;

  @CreationTimestamp //generated automatically by the database
  @Column(nullable = false)
  @JsonIgnore //this attribute should not be returned after GET or POST Request
  private Timestamp created;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public AD_TYPE getType() {
    return type;
  }

  public void setType(AD_TYPE type) {
    this.type = type;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}

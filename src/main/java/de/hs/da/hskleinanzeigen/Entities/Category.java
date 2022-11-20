package de.hs.da.hskleinanzeigen.Entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "CATEGORY")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(nullable = true)
  @JsonAlias("parentId")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Integer parent_id;

  @JsonProperty(namespace = "name")
  @NotEmpty(message = "you must give a name to the new category")
  private String name;

  public Category(Integer id, Integer parent_id, String name) {
    this.id = id;
    this.parent_id = parent_id;
    this.name = name;
  }

  public Category() {

  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getParent_id() {
    return parent_id;
  }

  public void setParent_id(Integer parent_id) {
    this.parent_id = parent_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Category{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}

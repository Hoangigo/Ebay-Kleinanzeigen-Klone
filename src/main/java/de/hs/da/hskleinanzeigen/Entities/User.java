package de.hs.da.hskleinanzeigen.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "`USER`")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false)
  @NotEmpty(message = "Eine Email Adresse müssen Sie eingeben")
  private String email;

  @Column(nullable = false)
  @NotEmpty(message = "Ein passwort fehlt noch")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @JsonProperty("firstName")
  private String first_name;
  //TODO ich darf bei post firstName nicht eingeben, ist es so gewollt? (payload unvollständig)

  @JsonProperty("lastName")
  private String last_name;

  private String phone;

  private String location;

  @CreationTimestamp
  @Column(nullable = false)
  @JsonIgnore
  private Timestamp created;

  public User() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirst_name() {
    return first_name;
  }

  public void setFirst_name(String first_name) {
    this.first_name = first_name;
  }

  public String getLast_name() {
    return last_name;
  }

  public void setLast_name(String last_name) {
    this.last_name = last_name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
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

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", first_name='" + first_name + '\'' +
        ", last_name='" + last_name + '\'' +
        ", phone='" + phone + '\'' +
        ", location='" + location + '\'' +
        '}';
  }
}

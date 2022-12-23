package de.hs.da.hskleinanzeigen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

  public static final String outputMessage = "User with the given id not found";

  public UserNotFoundException() {
    super(outputMessage);
  }
}

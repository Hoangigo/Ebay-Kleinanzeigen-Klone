package de.hs.da.hskleinanzeigen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailAlreadyExitsException extends RuntimeException{
  public static final String outputMessage = "This email is already used by an other User";

  public UserEmailAlreadyExitsException() {
    super(outputMessage);
  }
}

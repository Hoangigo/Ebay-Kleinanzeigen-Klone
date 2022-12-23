package de.hs.da.hskleinanzeigen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdNotFoundException extends RuntimeException{

  public static final String outputMessage = "Advertisement with this id not found";

  public AdNotFoundException() {
    super(outputMessage);
  }
}

package de.hs.da.hskleinanzeigen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PayloadIncorrectException extends RuntimeException {

  public PayloadIncorrectException(String message) {
    super(message);
  }
}

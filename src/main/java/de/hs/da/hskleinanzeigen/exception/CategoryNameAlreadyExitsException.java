package de.hs.da.hskleinanzeigen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CategoryNameAlreadyExitsException extends RuntimeException{

  public static final String outputMessage = "Category with the given name already exists";

  public CategoryNameAlreadyExitsException() {
    super(outputMessage);
  }
}

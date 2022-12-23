package de.hs.da.hskleinanzeigen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException{
  public static final String outputMessage = "Category with the given id not found";

  public CategoryNotFoundException() {
    super(outputMessage);
  }
}

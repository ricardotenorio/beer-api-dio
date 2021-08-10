package ricardotenorio.github.com.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {

  public BeerNotFoundException(String beerName) {
    super(String.format("Beer with name %s not found", beerName));
  }

  public BeerNotFoundException(Long id) {
    super(String.format("Beer with ID %s not found", id));
  }

}

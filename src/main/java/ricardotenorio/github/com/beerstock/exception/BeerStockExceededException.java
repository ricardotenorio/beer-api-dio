package ricardotenorio.github.com.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockExceededException extends Exception {

  public BeerStockExceededException(Long id) {
    super(String.format("Beer with Id %s exceeds the maximum stock capacity", id));
  }

}

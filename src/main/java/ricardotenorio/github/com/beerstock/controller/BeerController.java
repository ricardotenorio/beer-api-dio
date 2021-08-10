package ricardotenorio.github.com.beerstock.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ricardotenorio.github.com.beerstock.dto.BeerDTO;
import ricardotenorio.github.com.beerstock.exception.BeerAlreadyRegisteredException;
import ricardotenorio.github.com.beerstock.exception.BeerNotFoundException;
import ricardotenorio.github.com.beerstock.service.BeerService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController implements BeerControllerDocs {

  private final BeerService beerService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BeerDTO createBeer(@RequestBody @Valid BeerDTO beerDTO) throws
      BeerAlreadyRegisteredException {
    return beerService.createBeer(beerDTO);
  }

  @GetMapping("/{name}")
  public BeerDTO findByName(@PathVariable String name) throws
      BeerNotFoundException {
    return beerService.findByName(name);
  }

  @GetMapping
  public List<BeerDTO> listBeers() {
    return beerService.listAll();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) throws BeerNotFoundException {
    beerService.deleteById(id);
  }

}

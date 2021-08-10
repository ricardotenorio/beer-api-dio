package ricardotenorio.github.com.beerstock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;
import ricardotenorio.github.com.beerstock.dto.BeerDTO;
import ricardotenorio.github.com.beerstock.exception.BeerAlreadyRegisteredException;
import ricardotenorio.github.com.beerstock.exception.BeerNotFoundException;

import java.util.List;

@Api("Manages beer stock")
public interface BeerControllerDocs {

  @ApiOperation(value = "Beer creation")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Success beer created"),
      @ApiResponse(code = 400, message = "Missing required fields")
  })
  BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException;

  @ApiOperation(value = "Returns beer found by a given name")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Beer found"),
      @ApiResponse(code = 404, message = "Not found")
  })
  BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException;

  @ApiOperation(value = "Lists all beers registered")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "List of all beers registered")
  })
  List<BeerDTO> listBeers();

  @ApiOperation(value = "Deletes a beer given a valid id")
  @ApiResponses(value = {
      @ApiResponse(code = 204, message = "Beer deleted"),
      @ApiResponse(code = 404, message = "Beer not found")
  })
  void deleteById(@PathVariable Long id) throws BeerNotFoundException;

}

package ricardotenorio.github.com.beerstock.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ricardotenorio.github.com.beerstock.builder.BeerDTOBuilder;
import ricardotenorio.github.com.beerstock.dto.BeerDTO;
import ricardotenorio.github.com.beerstock.entity.Beer;
import ricardotenorio.github.com.beerstock.exception.BeerAlreadyRegisteredException;
import ricardotenorio.github.com.beerstock.exception.BeerNotFoundException;
import ricardotenorio.github.com.beerstock.mapper.BeerMapper;
import ricardotenorio.github.com.beerstock.repository.BeerRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

  private static final long INVALID_BEER_ID = 1L;

  @Mock
  private BeerRepository beerRepository;

  private BeerMapper beerMapper = BeerMapper.INSTANCE;

  @InjectMocks
  private BeerService beerService;

  @Test
  void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {

    // given
    BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);

    // when
    when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
    when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

    // then
    BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

    assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
    assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
    assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));

  }

  @Test
  void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {

    // given
    BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);

    // when
    when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

    // then
    assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));

  }

  @Test
  void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {

    // given
    BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

    // when
    when(beerRepository.findByName(expectedFoundBeer.getName()))
        .thenReturn(Optional.of(expectedFoundBeer));

    // then
    BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

    assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));

  }

  @Test
  void whenGivenBeerNameDoesntExistThenThrowAnException() {

    // given
    BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

    // when
    when(beerRepository.findByName(expectedFoundBeerDTO.getName()))
        .thenReturn(Optional.empty());

    // then
    assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));

  }

}

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
import ricardotenorio.github.com.beerstock.exception.BeerStockExceededException;
import ricardotenorio.github.com.beerstock.mapper.BeerMapper;
import ricardotenorio.github.com.beerstock.repository.BeerRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

  @Test
  void whenListBeerIsCalledThenReturnAListOfBeers() {

    // given
    BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

    // when
    when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

    // then
    List<BeerDTO> foundBeersDTO = beerService.listAll();

    assertThat(foundBeersDTO, is(not(empty())));
    assertThat(foundBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));

  }

  @Test
  void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {

    // when
    when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

    // then
    List<BeerDTO> foundBeersDTO = beerService.listAll();

    assertThat(foundBeersDTO, is(empty()));

  }

  @Test
  void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {

    // given
    BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

    // when
    when(beerRepository.findById(expectedDeletedBeerDTO.getId()))
        .thenReturn(Optional.of(expectedDeletedBeer));
    doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());

    // then
    beerService.deleteById(expectedDeletedBeerDTO.getId());

    verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
    verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());

  }

  // increment

  @Test
  void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {

    // given
    BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
    int quantityToIncrement = 10;
    int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantity() + quantityToIncrement;

    // when
    when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
    when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

    // then
    BeerDTO incrementedBeerDTO = beerService.increment(expectedBeerDTO.getId(), quantityToIncrement);

    assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));
    assertThat(expectedQuantityAfterIncrement, lessThanOrEqualTo(expectedBeerDTO.getMax()));

  }

  @Test
  void whenIncrementIsGreaterThanMaxThenThrowException() {

    // given
    BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
    int quantityToIncrement = 90;

    // when
    when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

    // then
    assertThrows(BeerStockExceededException.class,
        () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));

  }

  @Test
  void whenIncrementAfterSumIsGreaterThanMaxThenThrowException() {

    // given
    BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
    int quantityToIncrement = 45;

    // when
    when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

    // then
    assertThrows(BeerStockExceededException.class,
        () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));

  }

  @Test
  void whenDecrementAfterSubtractionIsLessThanZeroThenThrowException() {

    // given
    BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
    int quantityToDecrement = 20;

    // when
    when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

    // then
    assertThrows(BeerStockExceededException.class,
        () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));

  }

}

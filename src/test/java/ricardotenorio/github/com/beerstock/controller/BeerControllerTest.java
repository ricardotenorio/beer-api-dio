package ricardotenorio.github.com.beerstock.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import ricardotenorio.github.com.beerstock.builder.BeerDTOBuilder;
import ricardotenorio.github.com.beerstock.dto.BeerDTO;
import ricardotenorio.github.com.beerstock.exception.BeerNotFoundException;
import ricardotenorio.github.com.beerstock.service.BeerService;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ricardotenorio.github.com.beerstock.utils.JsonConvertionUtils.asJsonString;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

  private static final String BEER_API_URL_PATH = "/api/v1/beers";
  private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
  private static final String BEER_API_BUBPATH_DECREMENT_URL = "/decrement";
  private static final long VALID_BEER_ID = 1L;
  private static final long INVALID_BEER_ID = 2L;

  private MockMvc mockMvc;

  @Mock
  private BeerService beerService;

  @InjectMocks
  private BeerController beerController;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(beerController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
        .build();
  }

  @Test
  void whenPOSTIsCalledThenABeerIsCreated() throws Exception {

    // given
    BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

    // when
    when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);

    // then
    mockMvc.perform(
        post(BEER_API_URL_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(beerDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is(beerDTO.getName())))
        .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
        .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));

  }
  
  @Test
  void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
    
    // given
    BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    beerDTO.setBrand(null);
    
    
    // then
    mockMvc.perform(
        post(BEER_API_URL_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(beerDTO)))
        .andExpect(status().isBadRequest());
    
  }

  @Test
  void whenGETIsCalledWithValidNameThenOkStatus() throws Exception {

    // given
    BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

    // when
    when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

    // then
    mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
      .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(beerDTO.getName())))
        .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
        .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));

  }

  @Test
  void whenGETIsCalledWithoutAValidNameThenNotFoundStatusIsReturned() throws Exception {

    // given
    BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

    // when
    when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

    // then
    mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

  }

  @Test
  void whenGETListIsCalledThenOkStatusIsReturned() throws Exception {

    // given
    BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

    // when
    when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

    // then
    mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
        .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
        .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));

  }

}

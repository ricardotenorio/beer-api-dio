package ricardotenorio.github.com.beerstock.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ricardotenorio.github.com.beerstock.dto.BeerDTO;
import ricardotenorio.github.com.beerstock.entity.Beer;

@Mapper
public interface BeerMapper {

  BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

  Beer toModel(BeerDTO beerDTO);

  BeerDTO toDTO(Beer beer);

}

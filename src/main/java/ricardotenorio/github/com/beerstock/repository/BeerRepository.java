package ricardotenorio.github.com.beerstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ricardotenorio.github.com.beerstock.entity.Beer;

import java.util.Optional;

public interface BeerRepository extends JpaRepository<Beer, Long> {

  Optional<Beer> findByName(String name);
}

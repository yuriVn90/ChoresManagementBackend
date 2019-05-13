package playground.dal;

import org.springframework.data.repository.CrudRepository;

public interface RandomNumberGeneratorDao extends CrudRepository<RandomNumberGenerator, Long>  {

}

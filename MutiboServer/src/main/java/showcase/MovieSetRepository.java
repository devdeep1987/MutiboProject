package showcase;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by Devdeep on 1/24/2015.
 */
public interface MovieSetRepository extends CrudRepository<MovieSet, Long> {
    MovieSet findById(Long id);
}

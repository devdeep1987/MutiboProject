package showcase;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Created by Devdeep on 12/21/2014.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    User findByUsername(String username);
}

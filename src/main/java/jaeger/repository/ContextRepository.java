package jaeger.repository;

import jaeger.model.Context;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author James Renfro
 */
public interface ContextRepository extends MongoRepository<Context, String> {

}

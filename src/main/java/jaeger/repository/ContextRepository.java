package jaeger.repository;

import jaeger.model.Context;
import jaeger.repository.custom.ContextRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author James Renfro
 */
public interface ContextRepository extends MongoRepository<Context, String>, ContextRepositoryCustom {

}

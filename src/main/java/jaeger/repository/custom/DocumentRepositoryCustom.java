package jaeger.repository.custom;

import jaeger.model.Document;

import java.util.List;
import java.util.Map;

/**
 * @author James Renfro
 */
public interface DocumentRepositoryCustom {

    <T> Document update(String documentId, Map<String, List<T>> data);

}

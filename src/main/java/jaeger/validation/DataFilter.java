package jaeger.validation;

import jaeger.model.Value;

import java.util.List;

/**
 * @author James Renfro
 */
public interface DataFilter {

    List<Value> filter(String key, List<Value> values);

}

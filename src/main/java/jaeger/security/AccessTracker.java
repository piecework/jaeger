package jaeger.security;

import jaeger.enumeration.AlarmSeverity;
import jaeger.model.Entity;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author James Renfro
 */
public interface AccessTracker {

    public void alarm(AlarmSeverity severity, String message);

    public void alarm(AlarmSeverity severity, String message, Entity principal);

    public void alarm(AlarmSeverity severity, String message, String entityId);

    public void track(String secretId, String key, String reason, boolean isAnonymousAllowed);

}

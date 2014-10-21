package jaeger.identity;

/**
 * @author James Renfro
 */
public interface AuthenticationPrincipalConverter {

    public String convert(String original);

}

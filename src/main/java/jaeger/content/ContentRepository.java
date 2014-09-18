package jaeger.content;

import jaeger.model.ContentResource;

import java.io.IOException;
import java.util.List;

/**
 * @author James Renfro
 */
public interface ContentRepository {

    /*
     * Retrieves content from a location and marks it as 'checked out' so it cannot be edited until it is
     * checked back in again. This location can include a prefix to indicate the
     * appropriate scheme, such as file: or classpath:, but in the case where a scheme is
     * provided, a ContentProvider to handle that scheme must also be included in the application
     * context or an exception will be thrown.
     */
    ContentResource checkoutByLocation(ContentProfile profile, String location) throws IOException;

    /*
     * Retrieves all content associated with a specific process instance so it can be displayed to the
     * user
     */
    List<ContentResource> findAll(ContentProfile profile) throws IOException;

    /*
     * Retrieves content from a location. This location can include a prefix to indicate the
     * appropriate scheme, such as file: or classpath:, but in the case where a scheme is
     * provided, a ContentProvider to handle that scheme must also be included in the application
     * context or an exception will be thrown.
     */
    ContentResource findByLocation(ContentProfile profile, String location) throws IOException;

    /*
     * Expires a piece of content so it is no longer available to be found
     */
    boolean expireByLocation(ContentProfile profile, String location) throws IOException;

    /*
     * Publishes all content for a document
     */
    boolean publish(ContentProfile profile, String documentId) throws IOException;

    /*
     * Releases checked out content
     */
    boolean releaseByLocation(ContentProfile profile, String location) throws IOException;

    /*
     * Replaces an existing content resource, and assuming that the underlying provider is able to handle it, creates
     * a new version
     */
    ContentResource replace(ContentProfile profile, ContentResource contentResource, String location) throws IOException;

    /*
     * Stores content specific to a process, or if the processDefinitionKey is left null,
     * in a general purpose location.
     */
    ContentResource save(ContentProfile profile, ContentResource contentResource) throws IOException;

}

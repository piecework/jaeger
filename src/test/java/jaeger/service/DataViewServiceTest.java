package jaeger.service;

import com.google.common.collect.Sets;
import jaeger.enumeration.ContextType;
import jaeger.enumeration.FieldTag;
import jaeger.enumeration.ValidationType;
import jaeger.exception.ValidationException;
import jaeger.model.*;
import jaeger.repository.ContextRepository;
import jaeger.repository.DocumentRepository;
import jaeger.resource.DataView;
import jaeger.test.config.FongoConfiguration;
import jaeger.test.config.TestControllerConfiguration;
import jaeger.test.config.TestServiceConfiguration;
import jaeger.validation.Validation;
import jaeger.validation.ValidationService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class DataViewServiceTest {

    @InjectMocks
    private DataViewService dataViewService;

    @Mock
    private ContextRepository contextRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ValidationService validationService;


    @Before
    public void setup() {
        Field title = new Field.Builder()
                .name("title")
                .type(FieldTag.TEXT.getFieldType())
                .build();

        Field emailAddress = new Field.Builder()
                .name("emailAddress")
                .type(FieldTag.EMAIL.getFieldType())
                .build();

        Map<String, Field> fields = new HashMap<String, Field>();
        fields.put(title.getName(), title);
        fields.put(emailAddress.getName(), emailAddress);

        Context viewContext = new Context();
        viewContext.setContextId("9876");
        viewContext.setType(ContextType.VIEW);
        viewContext.setNamespace("BAR");
        viewContext.setFields(fields);

        Mockito.doReturn(viewContext)
                .when(contextRepository).findOne(eq("9876"));

        Context searchContext = new Context();
        searchContext.setType(ContextType.SEARCH);
        searchContext.setNamespace("BAR");
        searchContext.setFields(fields);

        Mockito.doReturn(Collections.singletonList(searchContext))
               .when(contextRepository).findByNamespacesAndTypes(Sets.newHashSet("BAR"), Sets.newHashSet(ContextType.SEARCH));

        Map<String, List<Value>> data = new HashMap<String, List<Value>>();
        data.put("title", Collections.singletonList(new Value("Some Special Title")));
        data.put("emailAddress", Collections.singletonList(new Value("jane@nowhere.com")));
        data.put("anotherHiddenField", Collections.singletonList(new Value("This is a secret searchers don't see")));

        Document document1 = new Document();
        document1.setNamespace("BAR");
        document1.setDocumentId("12345");
        document1.setData(data);

        Mockito.doReturn(Collections.singletonList(document1))
               .when(documentRepository).findByNamespaces(Sets.newHashSet("BAR"));

        Document document2 = new Document();
        document2.setNamespace("BAR");
        document2.setDocumentId("12346");
        document2.setData(data);

        Mockito.doReturn(document2)
                .when(documentRepository).findOne(eq("12346"));
    }

    @Test
    public void verifyGetExists() {
        DataView view = dataViewService.get(Sets.newHashSet("BAR"), null, "9876", "12346");
        assertEquals(2, view.getData().size());
    }

    @Test
    public void verifyGetDoesNotExist() {
        DataView view = dataViewService.get(Sets.newHashSet("BAR"), null, "9876", "12345");
        assertNull(view);
    }

    @Test
    public void verifyUpdateExists() throws ValidationException {
        Map<String, List<Value>> data = new HashMap<String, List<Value>>();
        data.put("title", Collections.singletonList(new Value("Some Special Title")));
        data.put("emailAddress", Collections.singletonList(new Value("jane@nowhere.com")));
        data.put("anotherHiddenField", Collections.singletonList(new Value("This is a secret searchers don't see")));

        Validation validation = new Validation.Builder()
                .hasError(false)
                .build();

        Mockito.doReturn(validation)
               .when(validationService).validate(any(Document.class), any(Context.class), any(Map.class), any(ValidationType.class), any(Entity.class));

        DataView view = dataViewService.update(Sets.newHashSet("BAR"), null, "9876", "12346", data, ValidationType.SUBMISSION, Mockito.mock(Entity.class));
        assertNotNull(view);
        assertEquals(2, view.getData().size());
    }

    @Test
    public void verifyUpdateDoesNotExist() throws ValidationException {
        Map<String, List<Value>> data = new HashMap<String, List<Value>>();
        data.put("title", Collections.singletonList(new Value("Some Special Title")));
        data.put("emailAddress", Collections.singletonList(new Value("jane@nowhere.com")));
        data.put("anotherHiddenField", Collections.singletonList(new Value("This is a secret searchers don't see")));

        DataView view = dataViewService.update(Sets.newHashSet("BAR"), null, "9876", "12345", data, ValidationType.SUBMISSION, Mockito.mock(Entity.class));
        assertNull(view);
    }

    @Test
    public void verifySearchEmpty() {
        List<DataView> views = dataViewService.search(Sets.newHashSet("FOO"));
        assertNotNull(views);
        assertEquals(0, views.size());
    }

    @Test
    public void verifySearchOne() {
        List<DataView> views = dataViewService.search(Sets.newHashSet("BAR"));
        assertNotNull(views);
        assertEquals(1, views.size());

        DataView view = views.get(0);
        assertEquals(2, view.getData().size());
    }


}
package jaeger.controller;

import jaeger.Utility;
import jaeger.exception.ResourceNotFoundException;
import jaeger.exception.UnauthorizedException;
import jaeger.model.*;
import jaeger.repository.ContextRepository;
import jaeger.repository.DocumentRepository;
import jaeger.resource.DataView;
import jaeger.test.config.FongoConfiguration;
import jaeger.test.config.TestControllerConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestControllerConfiguration.class, FongoConfiguration.class})
public class DocumentControllerTest {

    private static final String TEST_DOCUMENT_ID = "CONTROLLER-TEST-1";
    private static final String TEST_NAMESPACE = "controller-test";
    private static final String TEST_EDIT_CONTEXT_ID = "controller-test-edit-context";
    private static final String TEST_ANY_CONTEXT_ID = "controller-test-any-context";
    private static final String TEST_ATTACH_CONTEXT_ID = "controller-test-attach-context";


    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private DataController dataController;

    @Autowired
    private DocumentRepository documentRepository;

    @Before
    public void setup() {
        Field viewOnlyTestField1 = new Field.Builder()
                .name("test-field-1")
                .editable(false)
                .build();

        Field hiddenTestField1 = new Field.Builder()
                .name("hidden-test-field-1")
                .editable(false)
                .build();

        Field editableTestField1 = new Field.Builder()
                .name("test-field-1")
                .editable(true)
                .build();

        Field editableTestField2 = new Field.Builder()
                .name("test-field-2")
                .editable(true)
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(viewOnlyTestField1.getName(), new Value("Some value"));
        data.putOne(hiddenTestField1.getName(), new Value("This is never shown except for any"));
        Document document = new Document();
        document.setNamespace(TEST_NAMESPACE);
        document.setDocumentId(TEST_DOCUMENT_ID);
        document.setData(data);
        Set<String> keywords = Utility.keywords(document.getData());
        document.setKeywords(keywords);
        document = documentRepository.save(document);

        Context defaultContext = new Context();
        defaultContext.setContextId(document.getDocumentId());
        defaultContext.setNamespace(document.getNamespace());
        contextRepository.save(defaultContext);

        Context editContext = new Context();
        editContext.setContextId(TEST_EDIT_CONTEXT_ID);
        editContext.setNamespace(document.getNamespace());
        contextRepository.save(editContext);

        Context anyContext = new Context();
        anyContext.setContextId(TEST_ANY_CONTEXT_ID);
        anyContext.setNamespace(document.getNamespace());
        anyContext.setAllowAny(true);
        contextRepository.save(anyContext);

        Context attachmentContext = new Context();
        attachmentContext.setContextId(TEST_ATTACH_CONTEXT_ID);
        attachmentContext.setNamespace(document.getNamespace());
        attachmentContext.setAllowAny(false);
        contextRepository.save(attachmentContext);
    }

    @Test
    public void verifyReadDefault() throws ResourceNotFoundException, UnauthorizedException {

        DataView dataView = dataController.read(TEST_DOCUMENT_ID);

        Assert.assertEquals(TEST_DOCUMENT_ID, dataView.getDocumentId());
        Assert.assertEquals(TEST_NAMESPACE, dataView.getNamespace());

        Map<String, List<Value>> data = dataView.getData();
        Assert.assertEquals(1, data.size());
    }

    @Test
    public void verifyReadEditContext() throws ResourceNotFoundException, UnauthorizedException {

        DataView dataView = dataController.read(TEST_DOCUMENT_ID, TEST_EDIT_CONTEXT_ID);

        Assert.assertEquals(TEST_DOCUMENT_ID, dataView.getDocumentId());
        Assert.assertEquals(TEST_NAMESPACE, dataView.getNamespace());

        Map<String, List<Value>> data = dataView.getData();
        Assert.assertEquals(1, data.size());
        Assert.assertNull(data.get("hidden-test-field-1"));
    }

    @Test
    public void verifyReadAnyContext() throws ResourceNotFoundException, UnauthorizedException {

        DataView dataView = dataController.read(TEST_DOCUMENT_ID, TEST_ANY_CONTEXT_ID);

        Assert.assertEquals(TEST_DOCUMENT_ID, dataView.getDocumentId());
        Assert.assertEquals(TEST_NAMESPACE, dataView.getNamespace());

        Map<String, List<Value>> data = dataView.getData();
        Assert.assertEquals(2, data.size());
        Assert.assertEquals("This is never shown except for any", data.get("hidden-test-field-1").iterator().next().toString());
    }

    @Test
    public void verifyReadAttachContext() throws ResourceNotFoundException, UnauthorizedException {

        DataView dataView = dataController.read(TEST_DOCUMENT_ID, TEST_ATTACH_CONTEXT_ID);

        Assert.assertEquals(TEST_DOCUMENT_ID, dataView.getDocumentId());
        Assert.assertEquals(TEST_NAMESPACE, dataView.getNamespace());

        Map<String, List<Value>> data = dataView.getData();
        Assert.assertEquals(0, data.size());
        Assert.assertNull(data.get("hidden-test-field-1"));
    }

}
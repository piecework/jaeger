package jaeger.repository;

import jaeger.Utility;
import jaeger.model.Document;
import jaeger.model.ManyMap;
import jaeger.model.Value;
import jaeger.repository.DocumentRepository;
import jaeger.test.config.FongoConfiguration;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={FongoConfiguration.class})
public class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Before
    public void setup() {
        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne("test-field-1", new Value("Some value"));
        Document document = new Document();
        document.setDocumentId("TEST-1");
        document.setData(data);
        Set<String> keywords = Utility.keywords(document.getData());
        document.setKeywords(keywords);
        documentRepository.save(document);
    }

    @Test
    public void verifyFindOne() {
        Document document = documentRepository.findOne("TEST-1");
        Assert.assertEquals("TEST-1", document.getDocumentId());

        Map<String, List<Value>> actualData = document.getData();
        List<Value> values = actualData.get("test-field-1");
        Assert.assertEquals(1, values.size());
        Value value = values.iterator().next();
        Assert.assertEquals("Some value", value.toString());
    }

    @Test
    public void verifyUpdateExistingValue() {
        ManyMap<String, String> data = new ManyMap<String, String>();
        data.putOne("test-field-1", "Some new value");
        Document document = documentRepository.update("TEST-1", data);

        Assert.assertNotNull(document);
        Assert.assertEquals("TEST-1", document.getDocumentId());

        Map<String, List<Value>> actualData = document.getData();
        List<Value> values = actualData.get("test-field-1");
        Assert.assertEquals(1, values.size());
        Value value = values.iterator().next();
        Assert.assertEquals("Some new value", value.toString());

        Set<String> keywords = document.getKeywords();
        Assert.assertEquals(3, keywords.size());
        Assert.assertTrue(keywords.contains("somevalue"));
        Assert.assertTrue(keywords.contains("somenewvalue"));
        Assert.assertTrue(keywords.contains("TEST-1"));
    }


}
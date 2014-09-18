package jaeger.repository;

import jaeger.model.Document;
import jaeger.model.ManyMap;
import jaeger.repository.DocumentRepository;
import jaeger.test.config.FongoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={FongoConfiguration.class})
public class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Test
    public void verifyUpdateMultivaluedMap() {
        ManyMap<String, String> data = new ManyMap<String, String>();
        Document document = documentRepository.update("TEST", data);

        Assert.assertNotNull(document);
    }


}
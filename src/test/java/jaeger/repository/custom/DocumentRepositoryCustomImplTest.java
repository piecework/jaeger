package jaeger.repository.custom;

import com.google.common.collect.Sets;
import jaeger.model.Document;
import jaeger.repository.DocumentRepository;
import jaeger.test.config.FongoConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={FongoConfiguration.class})
public class DocumentRepositoryCustomImplTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Before
    public void setup() {
        Document document1 = new Document();
        document1.setNamespace("foo");
        documentRepository.save(document1);

        Document document2 = new Document();
        document2.setNamespace("foo");
        documentRepository.save(document2);

        Document document3 = new Document();
        document3.setNamespace("bar");
        documentRepository.save(document3);

        Document document4 = new Document();
        document4.setNamespace("fa");
        documentRepository.save(document4);
    }

    @After
    public void teardown() {
        documentRepository.deleteAll();
    }

    @Test
    public void verifyFindBySingleNamespace() {
        List<Document> documents = documentRepository.findByNamespaces(Collections.singleton("foo"));
        Assert.assertEquals(2, documents.size());
    }

    @Test
    public void verifyFindByMultipleNamespaces() {
        List<Document> documents = documentRepository.findByNamespaces(Sets.newHashSet("foo", "bar", "la"));
        Assert.assertEquals(3, documents.size());
    }


}
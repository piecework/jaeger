package jaeger.repository.custom;

import jaeger.enumeration.ContextType;
import jaeger.model.Context;
import jaeger.model.Document;
import jaeger.repository.ContextRepository;
import jaeger.repository.DocumentRepository;
import jaeger.test.config.FongoConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={FongoConfiguration.class})
public class ContextRepositoryCustomImplTest {

    @Autowired
    private ContextRepository contextRepository;

    @Before
    public void setup() {
        Context context1 = new Context();
        context1.setContextId("1");
        context1.setNamespace("foo");
        context1.setType(ContextType.SEARCH);
        contextRepository.save(context1);

        Context context2 = new Context();
        context2.setContextId("2");
        context2.setNamespace("foo");
        context2.setType(ContextType.SEARCH);
        contextRepository.save(context2);

        Context context3 = new Context();
        context3.setContextId("3");
        context3.setNamespace("foo");
        context3.setType(ContextType.SEARCH);
        contextRepository.save(context3);
    }

    @After
    public void teardown() {
        contextRepository.deleteAll();
    }

    @Test
    public void verifyFindNoneBySingleNamespaceAndSingleType() throws Exception {
        List<Context> contexts = contextRepository.findByNamespacesAndTypes(Collections.singleton("foo"), Collections.singleton(ContextType.ATTACH));
        assertEquals(0, contexts.size());
    }

    @Test
    public void verifyFindSomeBySingleNamespaceAndSingleType() throws Exception {
        List<Context> contexts = contextRepository.findByNamespacesAndTypes(Collections.singleton("foo"), Collections.singleton(ContextType.SEARCH));
        assertEquals(3, contexts.size());
    }

}
package jaeger.repository;

import jaeger.Utility;
import jaeger.enumeration.ContextType;
import jaeger.enumeration.PermissionType;
import jaeger.enumeration.PrincipalType;
import jaeger.model.Document;
import jaeger.model.ManyMap;
import jaeger.model.Permission;
import jaeger.model.Value;
import jaeger.test.config.FongoConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={FongoConfiguration.class})
public class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Before
    public void setup() {
        Permission searchPermission1 = new Permission(ContextType.SEARCH, PermissionType.NAMESPACE, PrincipalType.GROUP, "testgroup1", "testprocess1");
        permissionRepository.save(searchPermission1);
        Permission searchPermission2 = new Permission(ContextType.SEARCH, PermissionType.NAMESPACE, PrincipalType.GROUP, "testgroup1", "testprocess2");
        permissionRepository.save(searchPermission2);
        Permission searchPermission3 = new Permission(ContextType.SEARCH, PermissionType.NAMESPACE, PrincipalType.GROUP, "testgroup1", "testprocess3");
        permissionRepository.save(searchPermission3);
        Permission searchPermission4 = new Permission(ContextType.SEARCH, PermissionType.NAMESPACE, PrincipalType.GROUP, "testgroup2", "testprocess4");
        permissionRepository.save(searchPermission4);
        Permission searchPermission5 = new Permission(ContextType.SEARCH, PermissionType.NAMESPACE, PrincipalType.GROUP, "testgroup2", "testprocess1");
        permissionRepository.save(searchPermission5);

        Permission viewPermission1 = new Permission(ContextType.VIEW, PermissionType.NAMESPACE, PrincipalType.GROUP, "testgroup1", "testprocess1");
        permissionRepository.save(viewPermission1);
        Permission viewPermission2 = new Permission(ContextType.VIEW, PermissionType.NAMESPACE, PrincipalType.GROUP, "testgroup1", "testprocess2");
        permissionRepository.save(viewPermission2);

        Permission editPermission1 = new Permission(ContextType.EDIT, PermissionType.DOCUMENT, PrincipalType.PERSON, "testuser1", "12345");
        permissionRepository.save(editPermission1);
        Permission editPermission2 = new Permission(ContextType.EDIT, PermissionType.DOCUMENT, PrincipalType.PERSON, "testuser1", "12346");
        permissionRepository.save(editPermission2);
    }

    @After
    public void tearDown() {
        permissionRepository.deleteAll();
    }

    @Test
    public void verifyByNamespace() {
        List<Permission> permissions = permissionRepository.findByPrincipalTypeAndPrincipalId(PrincipalType.GROUP, "testgroup1");

        assertEquals(5, permissions.size());

        int countTestProcess1Search = 0;
        int countTestProcess1 = 0;
        for (Permission permission : permissions) {
            if (permission.getPermissionId().equals("testprocess1")) {
                countTestProcess1++;
                if (permission.getContextType() == ContextType.SEARCH)
                    countTestProcess1Search++;
            }
        }

        assertEquals(1, countTestProcess1Search);
        assertEquals(2, countTestProcess1);
    }

    @Test
    public void verifyByDocument() {
        List<Permission> permissions = permissionRepository.findByPrincipalTypeAndPrincipalId(PrincipalType.PERSON, "testuser1");

        assertEquals(2, permissions.size());

        int count12345 = 0;
        int count12346 = 0;
        for (Permission permission : permissions) {
            if (permission.getPermissionId().equals("12345"))
                count12345++;
            if (permission.getPermissionId().equals("12346"))
                count12346++;
        }

        assertEquals(1, count12345);
        assertEquals(1, count12346);
    }


}
/*
 *  Copyright 2014 University of Washington Licensed under the
 *	Educational Community License, Version 2.0 (the "License"); you may
 *	not use this file except in compliance with the License. You may
 *	obtain a copy of the License at
 *
 *  http://www.osedu.org/licenses/ECL-2.0
 *
 *	Unless required by applicable law or agreed to in writing,
 *	software distributed under the License is distributed on an "AS IS"
 *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *	or implied. See the License for the specific language governing
 *	permissions and limitations under the License.
 */
package jaeger.security;

import com.google.common.collect.Sets;
import jaeger.enumeration.PermissionType;
import jaeger.model.Permission;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author James Renfro
 */
public class PermissionAuthority implements GrantedAuthority {

    private final Set<String> editableDocumentIds = new HashSet<String>();
    private final Set<String> editableNamespaces = new HashSet<String>();
    private final Set<String> viewableDocumentIds = new HashSet<String>();
    private final Set<String> viewableNamespaces = new HashSet<String>();
    private final Set<String> searchableNamespaces = new HashSet<String>();

    public PermissionAuthority(List<Permission> permissions) {
        if (permissions != null) {
            for (Permission permission : permissions) {
                switch (permission.getContextType()) {
                    case EDIT:
                        if (permission.getPermissionType() == PermissionType.DOCUMENT)
                            editableDocumentIds.add(permission.getPermissionId());
                        else
                            editableNamespaces.add(permission.getPermissionId());
                        break;
                    case VIEW:
                        if (permission.getPermissionType() == PermissionType.DOCUMENT)
                            viewableDocumentIds.add(permission.getPermissionId());
                        else
                            viewableNamespaces.add(permission.getPermissionId());
                        break;
                    case SEARCH:
                        searchableNamespaces.add(permission.getPermissionId());
                        break;
                }
            }
        }
    }

    @Override
    public String getAuthority() {
        return toString();
    }

    public Set<String> editableDocumentIds() {
        return editableDocumentIds;
    }

    public Set<String> editableNamespaces() {
        return editableNamespaces;
    }

    public Set<String> searchableNamespaces() {
        return Sets.union(searchableNamespaces, viewableNamespaces());
    }

    public Set<String> viewableDocumentIds() {
        return Sets.union(viewableDocumentIds, editableDocumentIds);
    }

    public Set<String> viewableNamespaces() {
        return Sets.union(viewableNamespaces, editableNamespaces);
    }

    @Override
    public String toString() {
        return "PermissionAuthority {" +
                "editableDocumentIds=" + editableDocumentIds +
                ", editableNamespaces=" + editableNamespaces +
                ", viewableDocumentIds=" + viewableDocumentIds +
                ", viewableNamespaces=" + viewableNamespaces +
                ", searchableNamespaces=" + searchableNamespaces +
                '}';
    }

}

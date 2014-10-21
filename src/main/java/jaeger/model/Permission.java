/*
 * Copyright 2013 University of Washington
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaeger.model;

import jaeger.enumeration.ContextType;
import jaeger.enumeration.PermissionType;
import jaeger.enumeration.PrincipalType;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 *
 *
 *
 * @author James Renfro
 */
public class Permission {

    ContextType contextType;

    PermissionType permissionType;

    PrincipalType principalType;

    String principalId;

    String permissionId;

    public Permission() {

    }

    public Permission(ContextType contextType, PermissionType permissionType, PrincipalType principalType, String principalId, String permissionId) {
        this.contextType = contextType;
        this.permissionType = permissionType;
        this.principalType = principalType;
        this.principalId = principalId;
        this.permissionId = permissionId;
    }

    public boolean groupCanSearchByNamespace(String groupId, String namespace) {
        return hasPermission(ContextType.SEARCH, PermissionType.NAMESPACE, PrincipalType.GROUP, groupId, namespace);
    }

    protected boolean hasPermission(ContextType contextType, PermissionType permissionType, PrincipalType principalType, String principalId, String permissionId) {
        if (this.contextType == contextType && this.permissionType == permissionType
                && this.principalType == principalType && StringUtils.isNotEmpty(principalId)) {
            return this.principalId.equals(principalId) && this.permissionId.equals(permissionId);
        }
        return false;
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public PrincipalType getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(PrincipalType principalType) {
        this.principalType = principalType;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}

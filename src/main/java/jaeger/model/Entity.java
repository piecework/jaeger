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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import jaeger.security.PermissionAuthority;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Collections;
import java.util.Set;

/**
 * @author James Renfro
 */
public class Entity extends Value {

    public enum EntityType { SYSTEM, PERSON };

    @XmlTransient
    private final String entityId;

    @XmlTransient
    private final EntityType entityType;

    @XmlTransient
    private final PermissionAuthority permissionAuthority;

    private Entity actingAs;

    protected Entity(String entityId, EntityType entityType, PermissionAuthority permissionAuthority) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.permissionAuthority = permissionAuthority;
    }

    @JsonIgnore
    public PermissionAuthority getPermissionAuthority() {
        return permissionAuthority;
    }

//    @JsonIgnore
//    public boolean hasRole(String namespace, String... allowedRoles) {
//        if (namespace != null && StringUtils.isNotEmpty(namespace)) {
//            Set<String> allowedRoleSet = allowedRoles != null && allowedRoles.length > 0 ? Sets.newHashSet(allowedRoles) : null;
//            if (permissionAuthority.hasRole(namespace, allowedRoleSet))
//                return true;
//        }
//
//        return false;
//    }
//
//    @JsonIgnore
//    public Set<String> getProcessDefinitionKeys(String... allowedRoles) {
//        Set<String> allowedRoleSet = allowedRoles != null && allowedRoles.length > 0 ? Sets.newHashSet(allowedRoles) : null;
//        Set<String> processDefinitionKeys = permissionAuthority.getProcessDefinitionKeys(allowedRoleSet);
//
//        if (processDefinitionKeys == null)
//            return Collections.emptySet();
//
//        return processDefinitionKeys;
//    }

    @JsonIgnore
    public String getEntityId() {
        return entityId;
    }

    @JsonIgnore
    public EntityType getEntityType() {
        return entityType;
    }

    @JsonIgnore
    public void setActingAs(Entity actingAs) {
        this.actingAs = actingAs;
    }

    @JsonIgnore
    public Entity getActingAs() {
        return actingAs != null ? actingAs : this;
    }

    @JsonIgnore
    public String getActingAsId() {
        return actingAs != null ? actingAs.getEntityId() : this.getEntityId();
    }

    @Override
    public String toString() {
        return getEntityId();
    }
}

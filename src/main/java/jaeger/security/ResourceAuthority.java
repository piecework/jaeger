/*
 * Copyright 2012 University of Washington
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
package jaeger.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author James Renfro
 */
public class ResourceAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	
	private final String role;
	private final Set<String> namespaces;

    protected ResourceAuthority() {
        this(new Builder());
    }
	
	private ResourceAuthority(Builder builder) {
		this.role = builder.role;
		this.namespaces = builder.namespaces != null ? Collections.unmodifiableSet(builder.namespaces) : null;
	}

    public boolean hasRole(String namespace, Set<String> allowedRoleSet) {
        if (allowedRoleSet == null || allowedRoleSet.contains(getRole())) {
            Set<String> namespaces = getNamespaces();
            if (namespaces == null || namespaces.contains(namespace))
                return true;
        }
        return false;
    }

	public boolean isAuthorized(String roleAllowed, String namespaceAllowed) {
		if (roleAllowed == null)
			return false;
		return role.equals(roleAllowed) && (namespaceAllowed == null || namespaces.isEmpty() || namespaces.contains(namespaceAllowed));
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{").append(role);
		builder.append(":");
		if (namespaces != null && !namespaces.isEmpty()) {
            builder.append("[");
			int count = 1;
			int size = namespaces.size();
			for (String processDefinitionKey : namespaces) {
				builder.append(processDefinitionKey);
				
				if (count < size)
					builder.append(", ");
				count++;
			}
            builder.append("]");
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public String getAuthority() {
		return toString();
	}

    public Set<String> getNamespaces(Set<String> allowedRoleSet) {
        if (allowedRoleSet == null || allowedRoleSet.contains(getRole())) {
            Set<String> namespaces = getNamespaces();
            if (namespaces != null && !namespaces.isEmpty())
                return namespaces;
        }
        return Collections.emptySet();
    }

	public Set<String> getNamespaces() {
		return namespaces;
	}

	public String getRole() {
		return role;
	}


    public final static class Builder {

        private String role;
        private Set<String> namespaces;

        public Builder() {
            super();
        }

        public Builder(ResourceAuthority authority, Sanitizer sanitizer) {
            this.role = sanitizer.sanitize(authority.role);
            if (authority.namespaces != null && !authority.namespaces.isEmpty()) {
                this.namespaces = new HashSet<String>(authority.namespaces.size());
                for (String processDefinitionKey : authority.namespaces) {
                    this.namespaces.add(sanitizer.sanitize(processDefinitionKey));
                }
            }
        }

        public ResourceAuthority build() {
            return new ResourceAuthority(this);
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder namespace(String namespace) {
            if (this.namespaces == null)
                this.namespaces = new HashSet<String>();
            this.namespaces.add(namespace);
            return this;
        }

    }

}

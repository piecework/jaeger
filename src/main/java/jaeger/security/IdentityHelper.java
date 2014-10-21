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
package jaeger.security;

import jaeger.model.Application;
import jaeger.model.Entity;
import jaeger.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * @author James Renfro
 */
@Service
public class IdentityHelper {

    public Entity getPrincipal() {
        String systemName = null;
        IdentityDetails identity = null;
        Collection<? extends GrantedAuthority> authorities = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();

            if (authentication != null) {
                Object principalAsObject = authentication.getPrincipal();
                if (principalAsObject instanceof IdentityDetails)
                    identity = IdentityDetails.class.cast(principalAsObject);
                else if (authentication.getCredentials() != null && authentication.getCredentials() instanceof X509Certificate)
                    systemName = principalAsObject.toString();
                authorities = authentication.getAuthorities();
            }
        }

        PermissionAuthority accessAuthority = null;
        if (authorities != null && !authorities.isEmpty()) {
            for (GrantedAuthority authority : authorities) {
                if (authority instanceof PermissionAuthority) {
                    accessAuthority = PermissionAuthority.class.cast(authority);
                }
            }
        }

        if (identity == null) {
            if (systemName == null)
                return null;

            return new Application(systemName, accessAuthority);
        }

        return new User.Builder(identity).permissionAuthority(accessAuthority).build();
    }

}

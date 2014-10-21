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
package jaeger.identity;

import jaeger.properties.DebugProperties;
import jaeger.security.IdentityDetails;
import jaeger.security.PermissionAuthority;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

/**
 * @author James Renfro
 */
public class DebugUserDetailsService implements UserDetailsService {

    private final PermissionAuthority permissionAuthority;
    private final DebugProperties debugProperties;

    public DebugUserDetailsService(DebugProperties debugProperties, PermissionAuthority permissionAuthority) {
        this.debugProperties = debugProperties;
        this.permissionAuthority = permissionAuthority;
    }

    public void init() {

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String id = username;
        String displayName = username;

        if (StringUtils.isNotEmpty(debugProperties.getUserId()) && debugProperties.getUserId().equals(id))
            displayName = debugProperties.getDisplayName();

        UserDetails delegate = new org.springframework.security.core.userdetails.User(id, "none",
                Collections.singletonList(permissionAuthority));
        return new IdentityDetails(delegate, id, id, displayName, "");
    }

}

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
package jaeger.config;

import jaeger.Constants;
import jaeger.identity.AuthenticationPrincipalConverter;
import jaeger.identity.DebugIdentityService;
import jaeger.identity.DebugUserDetailsService;
import jaeger.identity.DisplayNameConverter;
import jaeger.model.Permission;
import jaeger.properties.DebugProperties;
import jaeger.properties.SecurityProperties;
import jaeger.security.KeyManagerCabinet;
import jaeger.security.PermissionAuthority;
import jaeger.service.IdentityService;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James Renfro
 */
@Configuration
@Profile(Constants.SpringProfiles.DEBUG_IDENTITY)
public class DebugIdentityConfiguration {

    private static final Logger LOGGER = Logger.getLogger(DebugIdentityConfiguration.class);

    @Bean
    public IdentityService identityService(DebugProperties debugProperties) throws Exception {
        List<Permission> permissions = new ArrayList<Permission>();
        PermissionAuthority permissionAuthority = new PermissionAuthority(permissions);
        DebugUserDetailsService debugUserDetailsService = new DebugUserDetailsService(debugProperties, permissionAuthority);
        debugUserDetailsService.init();
        return new DebugIdentityService(debugUserDetailsService);
    }

    @Bean
    public UserDetailsService userDetailsService(DebugProperties debugProperties) throws Exception {
        List<Permission> permissions = new ArrayList<Permission>();
        PermissionAuthority permissionAuthority = new PermissionAuthority(permissions);
        DebugUserDetailsService userDetailsService = new DebugUserDetailsService(debugProperties, permissionAuthority);
        userDetailsService.init();
        return userDetailsService;
    }

    @Bean
    public KeyManagerCabinet keyManagerCabinet(SecurityProperties securityProperties) throws Exception {
        return new KeyManagerCabinet.Builder(securityProperties).build();
    }

}

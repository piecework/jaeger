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
package jaeger.test.config;

import jaeger.controller.DocumentController;
import jaeger.model.User;
import jaeger.security.AccessAuthority;
import jaeger.security.IdentityHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static org.mockito.Matchers.any;

/**
 * @author James Renfro
 */
@Configuration
public class TestControllerConfiguration {

    @Bean
    public DocumentController documentController() {
        return new DocumentController();
    }

    @Bean
    public IdentityHelper identityHelper() {
        AccessAuthority accessAuthority = Mockito.mock(AccessAuthority.class);
        User testUser = Mockito.mock(User.class);
        IdentityHelper helper = Mockito.mock(IdentityHelper.class);

        Mockito.doReturn(true)
               .when(accessAuthority).hasGroup(any(Set.class));

        Mockito.doReturn(testUser)
               .when(helper).getPrincipal();

        Mockito.doReturn(accessAuthority)
               .when(testUser).getAccessAuthority();

        return helper;
    }

}

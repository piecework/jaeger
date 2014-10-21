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
package jaeger;

import jaeger.properties.DebugProperties;
import jaeger.properties.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Main entry point for microservice, that tells Spring how to begin autowiring
 * the application context.
 *
 * @author James Renfro
 */
@Configuration
@EnableAutoConfiguration
@EnableWebMvc
@ComponentScan
@PropertySource(value = {"file:/etc/jaeger/jaeger.properties"}, ignoreResourceNotFound = true)
@EnableConfigurationProperties({DebugProperties.class, MongoProperties.class, SecurityProperties.class})
public class BootConfiguration {

    @Bean
    public ServletRegistrationBean dispatcherServlet(ApplicationContext applicationContext) {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        return new ServletRegistrationBean(servlet, "/api/*");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication springApplication = new SpringApplication(BootConfiguration.class);
        springApplication.run(args);
    }

}

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

import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
import jaeger.model.Document;
import jaeger.properties.MongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.net.ssl.SSLSocketFactory;

/**
 * Configures Spring Data Mongo classes, setting the necessary options to use ssl if that value
 * is set in {@link jaeger.properties.MongoProperties} -- note that it also delegates to MongoProperties
 * to attach credentials and set connection information.
 *
 * @author James Renfro
 */
@Configuration
@EnableMongoRepositories(basePackages="jaeger.repository",repositoryImplementationPostfix="CustomImpl")
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    private MongoProperties mongoProperties;

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    @Primary
    public MongoDbFactory mongoDbFactory(Mongo mongo) throws Exception {
        String db = mongoProperties.getDatabase();
        return new SimpleMongoDbFactory(mongo, db);
    }

    @Bean
    @Primary
    public Mongo mongo() throws Exception {
        MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();

        if (mongoProperties.isUseSsl())
            optionsBuilder.socketFactory(SSLSocketFactory.getDefault());

        return mongoProperties.createMongoClient(optionsBuilder.build());
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        String bucket = mongoProperties.getGridFsDatabase();
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter(), bucket);
    }

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }


    @Override
    protected String getMappingBasePackage() {
        return Document.class.getPackage().getName();
    }

}

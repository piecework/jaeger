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
package jaeger.properties;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author James Renfro
 */
@ConfigurationProperties(prefix = "consolidate.mongo")
public class MongoProperties {

    private String serverAddresses;

    private String database = "test";

    private String gridFsDatabase;

    private String username;

    private String password;

    private boolean useSsl;

    public String getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(String serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void clearPassword() {
        this.password = null;
    }

    public String getGridFsDatabase() {
        return this.gridFsDatabase;
    }

    public void setGridFsDatabase(String gridFsDatabase) {
        this.gridFsDatabase = gridFsDatabase;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public MongoClient createMongoClient(MongoClientOptions options)
            throws UnknownHostException {
        try {
            List<ServerAddress> serverAddresses = new LinkedList<ServerAddress>();
            String mongoServerAddresses = getServerAddresses();
            if (!StringUtils.isEmpty(mongoServerAddresses)) {
                String[] addresses = mongoServerAddresses.split(",");
                for (String address : addresses) {
                    String ip = null;
                    int port = 27017;
                    String[] tokens = address.split(":");
                    if (tokens.length > 0) {
                        ip = tokens[0];
                        if (tokens.length > 1) {
                            port = Integer.parseInt(tokens[1]);
                        }
                    }
                    serverAddresses.add(new ServerAddress(ip, port));
                }
            } else {
                serverAddresses.add(new ServerAddress());
            }

            if (options == null) {
                options = MongoClientOptions.builder().build();
            }

            List<MongoCredential> credentials = null;
            if (this.password != null && this.username != null) {
                credentials = Arrays.asList(MongoCredential.createMongoCRCredential(
                        this.username, getDatabase(), this.password.toCharArray()));
            }
            return new MongoClient(serverAddresses, credentials, options);
        }
        finally {
//            clearPassword();
        }
    }

    private MongoClientOptions.Builder builder(MongoClientOptions options) {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        if (options != null) {
            builder.alwaysUseMBeans(options.isAlwaysUseMBeans());
            builder.connectionsPerHost(options.getConnectionsPerHost());
            builder.connectTimeout(options.getConnectTimeout());
            builder.cursorFinalizerEnabled(options.isCursorFinalizerEnabled());
            builder.dbDecoderFactory(options.getDbDecoderFactory());
            builder.dbEncoderFactory(options.getDbEncoderFactory());
            builder.description(options.getDescription());
            builder.maxWaitTime(options.getMaxWaitTime());
            builder.readPreference(options.getReadPreference());
            builder.socketFactory(options.getSocketFactory());
            builder.socketKeepAlive(options.isSocketKeepAlive());
            builder.socketTimeout(options.getSocketTimeout());
            builder.threadsAllowedToBlockForConnectionMultiplier(options
                    .getThreadsAllowedToBlockForConnectionMultiplier());
            builder.writeConcern(options.getWriteConcern());
        }
        return builder;
    }



}

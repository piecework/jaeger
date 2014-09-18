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
package jaeger.repository.custom;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import jaeger.Utility;
import jaeger.model.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author James Renfro
 */
@Service
@NoRepositoryBean
public class DocumentRepositoryCustomImpl implements DocumentRepositoryCustom {

    @Autowired
    private MongoOperations mongoOperations;

    private static final FindAndModifyOptions findAndModifyAndReturnNew = new FindAndModifyOptions().returnNew(true).upsert(true);

    @Override
    public <T> Document update(String documentId, Map<String, List<T>> data) {
        Query query = Query.query(Criteria.where("_id").is(documentId));
        Update update = constructUpdate(documentId, data);

        return mongoOperations.findAndModify(query, update, findAndModifyAndReturnNew, Document.class);
    }

    private <T> Update constructUpdate(String documentId, Map<String, List<T>> data) {
        Update update = new Update();

        for (Map.Entry<String, List<T>> entry : data.entrySet()) {
            String key = "data." + entry.getKey();
            List<T> values = entry.getValue();
            List<Object> dbObjects = new ArrayList<Object>();

            for (Object value : values) {
                if (value != null) {
                    dbObjects.add(convert(value));
                }
            }

            update.set(key, dbObjects);
        }

        Set<String> keywords = Utility.keywords(data);
        if (!keywords.isEmpty()) {
            BasicDBList eachList = new BasicDBList();
            for (String keyword : keywords) {
                eachList.add(keyword);
            }
            if (StringUtils.isNotEmpty(documentId))
                eachList.add(documentId);
            update.addToSet(
                    "keywords",
                    BasicDBObjectBuilder.start("$each", eachList).get()
            );
        }

        return update;
    }

    private Object convert(Object value) {
        Class<?> clz = null;
        if (value instanceof String)
            value = new Value((String)value);

        MongoConverter converter = mongoOperations.getConverter();
        MongoTypeMapper typeMapper = converter.getTypeMapper();

        Object dbObject = converter.convertToMongoType(value);

        if (value instanceof File)
            clz = File.class;
        else if (value instanceof User)
            clz = User.class;
        else if (value instanceof Secret)
            clz = Secret.class;
        else if (value instanceof DateValue)
            clz = DateValue.class;
        else if (value instanceof Value)
            clz = Value.class;

        if (clz != null)
            typeMapper.writeType(clz, DBObject.class.cast(dbObject));

        return dbObject;
    }

}

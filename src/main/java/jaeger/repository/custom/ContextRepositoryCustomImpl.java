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

import jaeger.enumeration.ContextType;
import jaeger.model.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author James Renfro
 */
@Service
@NoRepositoryBean
public class ContextRepositoryCustomImpl implements ContextRepositoryCustom {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public List<Context> findByNamespacesAndTypes(Set<String> namespaces, Set<ContextType> types) {
        Query query = Query.query(Criteria.where("namespace").in(namespaces).andOperator(Criteria.where("type").in(types)));
        return mongoOperations.find(query, Context.class);
    }

}

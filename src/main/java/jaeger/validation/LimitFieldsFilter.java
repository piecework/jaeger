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
package jaeger.validation;

import jaeger.enumeration.FieldTag;
import jaeger.model.Field;
import jaeger.model.Option;
import jaeger.model.Value;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Data filter that limits values to those that belong to the set of fields
 * used to instantiate the filter, and passed on the boolean flag passed,
 * includes or does not include values of restricted fields
 *
 * @author James Renfro
 */
public class LimitFieldsFilter implements DataFilter {

    private final Set<String> validKeys;

    public LimitFieldsFilter(Collection<Field> fields, boolean includeRestrictedFields) {
        this.validKeys = new HashSet<String>();
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                if (!includeRestrictedFields && field.isRestricted())
                    continue;

                String fieldName = field.getName();

                if (StringUtils.isEmpty(fieldName)) {
                    if (field.getType() != null && field.getType().equalsIgnoreCase(FieldTag.CHECKBOX.getFieldType())) {
                        List<Option> options = field.getOptions();
                        if (options != null) {
                            for (Option option : options) {
                                if (StringUtils.isNotEmpty(option.getName()))
                                    validKeys.add(option.getName());
                            }
                        }
                    }
                } else {
                    this.validKeys.add(fieldName);
                }
            }
        }
    }

    @Override
    public List<Value> filter(String key, List<Value> values) {
        if (validKeys.contains(key))
            return values;

        return Collections.emptyList();
    }

}

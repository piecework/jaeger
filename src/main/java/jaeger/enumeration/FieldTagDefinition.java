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
package jaeger.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author James Renfro
 */
public class FieldTagDefinition {

    private final String tagName;
    private final FieldAttributeDefinition[] attributeDefinitions;

    public FieldTagDefinition(String tagName, FieldAttributeDefinition ... attributeDefinitions) {
        this.tagName = tagName;
        this.attributeDefinitions = attributeDefinitions;
    }

    public String getTagName() {
        return tagName;
    }

    public Map<String, String> getAttributes() {
        Map<String, String> map = new HashMap<String, String>();
        for (FieldAttributeDefinition attributeDefinition : attributeDefinitions) {
            map.put(attributeDefinition.getName(), attributeDefinition.getValue());
        }
        return map;
    }
}

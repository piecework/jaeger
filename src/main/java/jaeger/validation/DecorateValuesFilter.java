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

import jaeger.enumeration.AccessLevel;
import jaeger.model.*;
import jaeger.security.AccessChecker;
import jaeger.security.concrete.PassthroughSanitizer;
import jaeger.service.IdentityService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.*;

/**
 * Data filter that decorates default values and rebuilds file values to include
 * a link that can be used to access the file contents.
 *
 * @author James Renfro
 */
public class DecorateValuesFilter implements DataFilter {
    private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

    private final Map<String, Value> defaultValueMap;
    private final PassthroughSanitizer passthroughSanitizer;
    private final Document document;
    private final IdentityService identityService;

    public DecorateValuesFilter(Document document, Context context, Collection<Field> fields, Entity principal, AccessChecker accessChecker, IdentityService identityService) {
        this.defaultValueMap = new HashMap<String, Value>();
        this.passthroughSanitizer = new PassthroughSanitizer();
        this.document = document;
        this.identityService = identityService;
        if (fields != null && !fields.isEmpty()) {
            AccessLevel accessLevel = accessChecker.check(document, context, principal);
            for (Field field : fields) {
                String defaultValueString = field.getDefaultValue();
                Value defaultValue = null;
                if (StringUtils.isNotEmpty(defaultValueString)) {
                    // Only populate the current user default value if the access level is edit
                    if (defaultValueString.equals("{{CurrentUser}}") && accessLevel == AccessLevel.EDIT)
                        defaultValue = principal != null ? principal.getActingAs() : null; //principal != null ? principal.getActingAs() : "";
                    else if (defaultValueString.equals("{{CurrentDate}}"))
                        defaultValue = new Value(dateTimeFormatter.print(new Date().getTime()));
                    else if (defaultValueString.contains("{{ConfirmationNumber}}") && document != null)
                        defaultValue = new Value(defaultValueString.replaceAll("\\{\\{ConfirmationNumber\\}\\}", document.getDocumentId()));
                    else
                        defaultValue = new Value(defaultValueString);

                    defaultValueMap.put(field.getName(), defaultValue);
                }
            }
        }
    }

    @Override
    public List<Value> filter(String key, List<Value> values) {
        if (isEmpty(values)) {
            Value defaultValue = defaultValueMap.get(key);
            if (defaultValue != null)
                return Collections.singletonList(defaultValue);

            return Collections.emptyList();
        }

        List<Value> list = new ArrayList<Value>(values.size());
        for (Value value : values) {
            if (value instanceof File) {
                File file = File.class.cast(value);

                List<Version> undecoratedVersions = file.getVersions();
                List<Version> versions = new ArrayList<Version>();
                if (document != null && undecoratedVersions != null && !undecoratedVersions.isEmpty()) {
                    Set<String> userIds = new HashSet<String>();
                    for (Version undecoratedVersion : undecoratedVersions) {
                        if (StringUtils.isNotEmpty(undecoratedVersion.getCreatedBy()))
                            userIds.add(undecoratedVersion.getCreatedBy());
                    }
                    Map<String, User> userMap = new HashMap<String, User>();
                    if (!userIds.isEmpty() && identityService != null)
                        userMap = identityService.findUsers(userIds);
                    for (Version undecoratedVersion : undecoratedVersions) {
                        versions.add(new Version(undecoratedVersion, userMap));
                    }
                }

                list.add(new File.Builder(file, passthroughSanitizer)
                        .processDefinitionKey(document != null ? document.getNamespace() : null)
                        .processInstanceId(document != null ? document.getNamespace() : null)
                        .fieldName(key)
                        .versions(versions)
                        .build());
            } else {
                list.add(value);
            }
        }

        return list;
    }

    private static boolean isEmpty(List<Value> values) {
        if (values == null || values.isEmpty())
            return true;

        boolean isEmpty = true;
        for (Value value : values) {
            if (value != null && !value.isEmpty()) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }

}

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

import jaeger.Registry;
import jaeger.Utility;
import jaeger.exception.ValidationRuleException;
import jaeger.model.*;
import jaeger.repository.DocumentRepository;
import jaeger.security.AccessChecker;
import jaeger.security.AccessTracker;
import jaeger.security.EncryptionService;
import jaeger.service.IdentityService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author James Renfro
 */
public class ValidationService {

    private final static Logger LOGGER = Logger.getLogger(ValidationService.class.getName());

    public enum ValidationType { VALIDATION, SAVE, SUBMISSION };

    @Autowired
    private AccessChecker accessChecker;

    @Autowired
    private AccessTracker accessTracker;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private Registry registry;


    public <T> Validation validate(Document document, Context context, Map<String, List<T>> data, ValidationType type, Entity entity) {
        Map<String, Field> fields = context.getFields();

        // Don't want to keep doing null checks below
        if (fields == null)
            fields = Collections.emptyMap();

        String reason = "User is submitting data that needs to be validated";

        Validation.Builder validationBuilder = new Validation.Builder();

        // TODO:
        //   1. Decrypt any data in the existing document for fields that are viewable for this context
        //   2. Loop through each field and validate any defined fields
        //   3. If a field passes validation, then add it to the list of fields to be updated
        //   4. If a field does not pass validation, then add it to the list of error messages to return

        if (data != null) {
            Map<String, List<Value>> decryptedInstanceData = decryptData(document, context, entity);

            Map<String, List<T>> validData = new HashMap<String, List<T>>();
            for (Map.Entry<String, List<T>> entry : data.entrySet()) {
                String fieldName = entry.getKey();

                // If a field exists, then it should be used to validate the data for this field name
                Field field = fields.get(fieldName);

                boolean isValid = false;

                if (field == null && context.isAllowAny()) {
                    isValid = true;
                } else if (field != null) {
                    Set<ValidationRule> rules = Utility.validationRules(field, registry);

                    if (rules != null) {
                        isValid = true;
                        for (ValidationRule rule : rules) {
                            try {
                                rule.evaluate(data, decryptedInstanceData, type == ValidationType.SAVE);
                            } catch (ValidationRuleException e) {
                                LOGGER.warning("Invalid input: " + e.getMessage() + " " + e.getRule());
                                validationBuilder.error(rule.getName(), e.getMessage());
                                isValid = false;
                            }
                        }
                    }
                }

                if (isValid)
                    validData.put(fieldName, entry.getValue());

            }

            // For anything other than pure validation, persist any data that's valid
            if (type != ValidationType.VALIDATION && !validData.isEmpty())
                documentRepository.update(document.getDocumentId(), validData);
        }

        return validationBuilder.build();
    }

    private Map<String, List<Value>> decryptData(Document document, Context context, Entity entity) {
        Collection<Field> fields = context.getFields().values();
        String reason = "To validate new input";
        Map<String, List<Value>> data = document.getData();
        DataFilter decryptValuesFilter = new DecryptValuesFilter(document, reason, accessTracker, encryptionService, entity, false);
        DataFilter limitFieldsFilter = context.isAllowAny() ? new NoOpFilter() : new LimitFieldsFilter(fields, true);
        DataFilter decorateValuesFilter = new DecorateValuesFilter(document, context, fields, entity, accessChecker, identityService);
        return Utility.filter(fields, data, limitFieldsFilter, decryptValuesFilter, decorateValuesFilter);
    }

}

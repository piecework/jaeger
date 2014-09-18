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

import jaeger.model.*;
import jaeger.security.EncryptionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * @author James Renfro
 */
public class ValidationService {

    @Autowired
    private EncryptionService encryptionService;

    // TODO: This should replace the ValidationFactory logic with something much simpler
    public <T> Validation validate(Document document, Context context, Map<String, List<T>> data) {
        Set<Field> fields = context.getFields();
//        Map<Field, List<ValidationRule>> fieldRuleMap = template.getFieldRuleMap();
//        Set<String> allFieldNames = Collections.unmodifiableSet(new HashSet<String>(template.getFieldMap().keySet()));
//        Set<String> fieldNames = new HashSet<String>(template.getFieldMap().keySet());

        Map<String, List<Value>> instanceData = document != null ? document.getData() : null;
        Map<String, List<T>> submissionData = data;

        Map<String, List<Value>> decryptedInstanceData = null;
        Map<String, List<Value>> decryptedSubmissionData = null;
        String reason = "User is submitting data that needs to be validated";

        Validation.Builder validationBuilder = new Validation.Builder();

        // TODO:
        //   1. Decrypt any data in the existing document for fields that are viewable for this context
        //   2. Loop through each field and validate any defined fields
        //   3. If a field passes validation, then add it to the list of fields to be updated
        //   4. If a field does not pass validation, then add it to the list of error messages to return

        if (fields != null) {
            for (Field field : fields) {


            }
        }


//        if (fields != null) {
//            decryptedSubmissionData = dataFilterService.allSubmissionData(modelProvider, submission, reason);
//
//            task = ModelUtility.task(modelProvider);
//            if (task != null)
//                decryptedInstanceData = dataFilterService.authorizedInstanceData(modelProvider, fields, version, reason, isAllowAny);
//
//            for (Field field : fields) {
//                List<ValidationRule> rules = fieldRuleMap != null && field.getName() != null ? fieldRuleMap.get(field) : Collections.<ValidationRule>emptyList();
//                validateField(modelProvider, validationBuilder, field, rules, fieldNames, submissionData, instanceData, decryptedSubmissionData, decryptedInstanceData, onlyAcceptValidInputs, false);
//            }
//        }
//
//        if (isAllowAny) {
//            if (!submissionData.isEmpty()) {
//                if (task == null)
//                    task = ModelUtility.task(modelProvider);
//                if (task != null)
//                    decryptedInstanceData = dataFilterService.authorizedInstanceData(modelProvider, Collections.<Field>emptySet(), version, reason, isAllowAny);
//
//                if (decryptedSubmissionData == null)
//                    decryptedSubmissionData = dataFilterService.allSubmissionData(modelProvider, submission, reason);
//
//                for (Map.Entry<String, List<Value>> entry : submissionData.entrySet()) {
//                    String fieldName = entry.getKey();
//
//                    if (!allFieldNames.contains(fieldName)) {
//                        Field field = new Field.Builder()
//                                .name(fieldName)
//                                .type(null)
//                                .maxInputs(10)
//                                .build();
//
//                        validateField(modelProvider, validationBuilder, field, Collections.<ValidationRule>emptyList(), fieldNames, submissionData, instanceData, decryptedSubmissionData, decryptedInstanceData, onlyAcceptValidInputs, true);
//                    }
//                }
//            }
//        }

        return validationBuilder.build();
    }


}

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

import com.google.common.base.Strings;
import jaeger.model.Secret;
import jaeger.model.Value;
import jaeger.security.EncryptionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data filter that decrypts and then masks all restricted values to their
 * appropriate plaintext length. The encrypted values are not returned from the
 * filter method; only the masked values are returned.
 *
 * @author James Renfro
 */
public class MaskRestrictedValuesFilter implements DataFilter {

    private static final Logger LOG = Logger.getLogger(MaskRestrictedValuesFilter.class);

    private final EncryptionService encryptionService;

    private final String namespace;
    private final String documentId;
    private final String coreMessage;

    public MaskRestrictedValuesFilter(String namespace, String documentId, String entityId, EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
        this.namespace = namespace;
        this.documentId = documentId;
        this.coreMessage = message(namespace, documentId, entityId);
    }

    @Override
    public List<Value> filter(String key, List<Value> values) {
        if (values == null || values.isEmpty())
            return Collections.emptyList();

        List<Value> list = new ArrayList<Value>(values.size());
        for (Value value : values) {
            if (value instanceof Secret) {
                Secret secret = Secret.class.cast(value);
                try {
                    String plaintext = encryptionService.decrypt(secret);
                    list.add(new Value(Strings.repeat("*", plaintext.length())));
                    if (LOG.isInfoEnabled())
                        LOG.info("Masking value of " + coreMessage);
                } catch (Exception exception) {
                    LOG.error("Failed to mask value of " + coreMessage, exception);
                }
            } else {
                list.add(value);
            }
        }

        return list;
    }

    private static String message(String processDefinitionKey, String processInstanceId, String entityId) {
        StringBuilder message = new StringBuilder(" restricted field for process ").append(processDefinitionKey);

        if (StringUtils.isNotEmpty(processInstanceId))
            message.append(" and instance ").append(processInstanceId);

        message.append(" on behalf of ");
        if (StringUtils.isEmpty(entityId))
            message.append("anonymous submitter");
        else
            message.append(entityId);

        return message.toString();
    }
}

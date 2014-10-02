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

import jaeger.enumeration.AlarmSeverity;
import jaeger.model.Document;
import jaeger.model.Entity;
import jaeger.model.Secret;
import jaeger.model.Value;
import jaeger.security.AccessTracker;
import jaeger.security.EncryptionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data filter that decrypts values passed, and calls AccessTracker to log the fact
 * that restricted data is being decrypted, including a reason why and who has
 * requested access to it
 *
 * @author James Renfro
 */
public class DecryptValuesFilter implements DataFilter {

    private static final Logger LOG = Logger.getLogger(DecryptValuesFilter.class);
    private static final String ANONYMOUS_RESTRICTED_MESSAGE = "Anonymous principals should never have access to restricted data. System will not decrypt this value ";

    private final String reason;
    private final AccessTracker accessTracker;
    private final EncryptionService encryptionService;
    private final Entity entity;
    private final boolean isAnonymousDecryptAllowed;

    private final String coreMessage;

    public DecryptValuesFilter(Document document, String reason, AccessTracker accessTracker, EncryptionService encryptionService, Entity entity, boolean isAnonymousDecryptAllowed) {
        this.reason = reason;
        this.accessTracker = accessTracker;
        this.encryptionService = encryptionService;
        this.isAnonymousDecryptAllowed = isAnonymousDecryptAllowed;
        this.entity = entity;
        this.coreMessage = message(document.getNamespace(), document.getDocumentId(), entity, isAnonymousDecryptAllowed);
    }

    @Override
    public List<Value> filter(String key, List<Value> values) {
        if (values == null || values.isEmpty())
            return Collections.emptyList();

        List<Value> list = new ArrayList<Value>(values.size());
        for (Value value : values) {
            if (value instanceof Secret) {
                // The only time we should ever anonymously decrypt anything is for submission data on a process that is allowed to submit anonymous
                // submission data, when no instance yet exists...
                if ((entity == null || StringUtils.isEmpty(entity.getEntityId())) && !isAnonymousDecryptAllowed) {
                    accessTracker.alarm(AlarmSeverity.URGENT, new StringBuilder(ANONYMOUS_RESTRICTED_MESSAGE).append(key).append(coreMessage).toString());
                    continue;
                }
                Secret secret = Secret.class.cast(value);
                try {
                    accessTracker.track(secret.getId(), key, reason, isAnonymousDecryptAllowed);
                    String plaintext = encryptionService.decrypt(secret);
                    list.add(new Value(plaintext));
                    if (LOG.isInfoEnabled())
                        LOG.info(new StringBuilder("Decrypting ").append(key).append(coreMessage).toString());
                } catch (Exception exception) {
                    LOG.error(new StringBuilder("Failed to decrypt ").append(key).append(coreMessage).toString(), exception);
                }
            } else {
                list.add(value);
            }
        }

        return list;
    }

    private static String message(String namespace, String documentId, Entity entity, boolean isAnonymousDecryptAllowed) {
        StringBuilder message = new StringBuilder(" restricted field for namespace ").append(namespace);

        if (StringUtils.isNotEmpty(documentId))
            message.append(" and instance ").append(documentId);

        message.append(" on behalf of ");
        if (entity == null && isAnonymousDecryptAllowed)
            message.append("anonymous submitter");
        else
            message.append(entity.getEntityId());

        return message.toString();
    }

}

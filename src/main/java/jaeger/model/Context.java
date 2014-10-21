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
package jaeger.model;

import jaeger.enumeration.ContextType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Map;
import java.util.Set;

/**
 * A context determines who is able to read or write to a document, and which fields are
 * viewable, editable, and required.
 *
 * Permissions to view or edit can be applied at the namespace or based on a specific
 *
 *
 * @author James Renfro
 */
public class Context {

    @Indexed
    private String namespace;

    @Indexed
    private ContextType type;

    @Id
    private String contextId;

    private Map<String, Field> fields;

    private boolean allowAttachments;

    private long maxAttachmentSize;

    private boolean allowAny;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public ContextType getType() {
        return type;
    }

    public void setType(ContextType type) {
        this.type = type;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public void setFields(Map<String, Field> fields) {
        this.fields = fields;
    }

    public boolean isAllowAttachments() {
        return allowAttachments;
    }

    public void setAllowAttachments(boolean allowAttachments) {
        this.allowAttachments = allowAttachments;
    }

    public long getMaxAttachmentSize() {
        return maxAttachmentSize;
    }

    public void setMaxAttachmentSize(long maxAttachmentSize) {
        this.maxAttachmentSize = maxAttachmentSize;
    }

    public boolean isAllowAny() {
        return allowAny;
    }

    public void setAllowAny(boolean allowAny) {
        this.allowAny = allowAny;
    }
}

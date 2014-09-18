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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

/**
 * A context determines who is able to read or write to a document, and which fields are
 * viewable, editable, and required
 *
 * @author James Renfro
 */
public class Context {

    @Indexed
    private String namespace;

    @Id
    private String contextId;

    private Set<Field> fields;

    private Set<String> viewGroups;

    private Set<String> editGroups;

    private boolean allowAttachments;

    private long maxAttachmentSize;

    private boolean allowAny;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public Set<String> getViewGroups() {
        return viewGroups;
    }

    public void setViewGroups(Set<String> viewGroups) {
        this.viewGroups = viewGroups;
    }

    public Set<String> getEditGroups() {
        return editGroups;
    }

    public void setEditGroups(Set<String> editGroups) {
        this.editGroups = editGroups;
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

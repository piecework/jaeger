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
package jaeger.resource;

import jaeger.Utility;
import jaeger.model.Attachment;
import jaeger.model.Context;
import jaeger.model.Document;
import jaeger.model.Value;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Map;

/**
 * @author James Renfro
 */
public class DataView extends ResourceSupport {

    private final String namespace;
    private final String documentId;
    private Map<String, String> metadata;
    private final Map<String, List<Value>> data;
    private final List<Attachment> attachments;

    public DataView(Document document, Context context) {
        this.namespace = document.getNamespace();
        this.documentId = document.getDocumentId();

        this.metadata = document.getMetadata();
        this.data = Utility.filter(document.getData(), context, false);

        if (context != null && context.isAllowAttachments()) {
            this.attachments = document.getAttachments();
        } else {
            this.attachments = null;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, List<Value>> getData() {
        return data;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

}

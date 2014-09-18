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

import jaeger.Utility;

import java.util.List;
import java.util.Map;

/**
 * @author James Renfro
 */
public class DocumentView {

    private final String namespace;
    private final String documentId;
    private final Map<String, List<Value>> data;
    private final List<Attachment> attachments;

    public DocumentView(Document document, Context context) {
        this.namespace = document.getNamespace();
        this.documentId = document.getDocumentId();

        this.data = Utility.filter(document.getData(), context, false);

        if (context.isAllowAttachments()) {
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

    public Map<String, List<Value>> getData() {
        return data;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}

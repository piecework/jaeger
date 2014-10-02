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

import jaeger.model.bind.FormNameMessageMapAdapter;
import jaeger.model.bind.FormNameValueEntryMapAdapter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author James Renfro
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "instance")
public class Document {

    @Indexed
    private String namespace;

    @Id
    private String documentId;

    private Map<String, String> metadata;

    @XmlJavaTypeAdapter(FormNameValueEntryMapAdapter.class)
    private Map<String, List<Value>> data;

    private Set<String> keywords;

    @XmlJavaTypeAdapter(FormNameMessageMapAdapter.class)
    private Map<String, List<Message>> messages;

    private List<Attachment> attachments;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Map<String, List<Value>> getData() {
        return data;
    }

    public void setData(Map<String, List<Value>> data) {
        this.data = data;
    }

    public Map<String, List<Message>> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, List<Message>> messages) {
        this.messages = messages;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}

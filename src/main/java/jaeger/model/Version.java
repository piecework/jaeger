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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * @author James Renfro
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Version implements Serializable {

    private final String label;
    private final String createdBy;
    private final long createDate;
    private final String contentId;
    private final String location;
    private transient final User createdByUser;

    public Version() {
        this.label = null;
        this.createdBy = null;
        this.createDate = 0l;
        this.contentId = null;
        this.location = null;
        this.createdByUser = null;
    }

    public Version(Version version, Map<String, User> userMap) {
        this.label = version.getLabel();
        this.createdBy = version.getCreatedBy();
        this.createDate = version.getCreateDate();
        this.contentId = version.getContentId();
        this.location = version.getLocation();
        this.createdByUser = version.getCreatedBy() != null ? userMap.get(version.getCreatedBy()) : null;
    }

    public Version(String label, String createdBy, long createDate, String id, String location) {
        this.label = label;
        this.createdBy = createdBy;
        this.createDate = createDate;
        this.contentId = id;
        this.location = location;
        this.createdByUser = null;
    }

    public String getLabel() {
        return label;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getCreateDate() {
        return createDate;
    }

    public String getContentId() {
        return contentId;
    }

    public String getLocation() {
        return location;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

}

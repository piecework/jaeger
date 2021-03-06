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
package jaeger.security;

import jaeger.enumeration.AccessLevel;
import jaeger.model.Context;
import jaeger.model.Document;
import jaeger.model.Entity;

/**
 *
 * @author James Renfro
 */
public class AccessChecker {

    /**
     * Determines if a given user is authorized to view or edit a document based on the provided context.
     */
    public AccessLevel check(Document document, Context context, Entity principal) {

//        if (principal != null && principal.getPermissionAuthority() != null && principal.getPermissionAuthority().hasGroup(context.getEditGroups()))
//            return AccessLevel.EDIT;
//
//        if (principal != null && principal.getPermissionAuthority() != null && principal.getPermissionAuthority().hasGroup(context.getViewGroups()))
//            return AccessLevel.VIEW;

        return AccessLevel.NONE;
    }

}

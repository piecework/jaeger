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
package jaeger;

/**
 * @author James Renfro
 */
public class Constants {

    public static class ConstraintTypes {
        public static final String AND = "AND";
        public static final String OR = "OR";
        public static final String IS_ALL_VALUES_MATCH = "IS_ALL_VALUES_MATCH";
        public static final String IS_CONFIRMATION_NUMBER = "IS_CONFIRMATION_NUMBER";
        public static final String IS_EMAIL_ADDRESS = "IS_EMAIL_ADDRESS";
        public static final String IS_NUMERIC = "IS_NUMERIC";
        public static final String IS_ONLY_REQUIRED_WHEN = "IS_ONLY_REQUIRED_WHEN";
        public static final String IS_ONLY_VISIBLE_WHEN = "IS_ONLY_VISIBLE_WHEN";
        public static final String IS_LIMITED_TO = "IS_LIMITED_TO";
        public static final String IS_STATE = "IS_STATE";
        public static final String SCREEN_IS_DISPLAYED_WHEN_ACTION_TYPE = "SCREEN_IS_DISPLAYED_WHEN_ACTION_TYPE";
    }

    public static class ValidationStatus {
        public static final String ERROR = "error";
        public static final String SUCCESS = "success";
        public static final String WARNING = "warning";
    }

}

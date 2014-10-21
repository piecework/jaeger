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

import com.google.common.collect.Sets;
import jaeger.controller.DataController;
import jaeger.enumeration.FieldTag;
import jaeger.exception.ResourceNotFoundException;
import jaeger.model.*;
import jaeger.validation.DataFilter;
import jaeger.validation.ValidationRule;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author James Renfro
 */
public class Utility {

    private static final Set<String> FREEFORM_INPUT_TYPES = Sets.newHashSet(FieldTag.FieldTypes.TEXT, FieldTag.FieldTypes.TEXTAREA, FieldTag.FieldTypes.PERSON, "current-date", "current-user");
            //"textarea", "person-lookup", "current-date", "current-user");

    public static boolean hasConstraint(String type, List<Constraint> constraints) {
        return getConstraint(type, constraints) != null;
    }

    public static Constraint getConstraint(String type, List<Constraint> constraints) {
        if (constraints != null && !constraints.isEmpty()) {
            for (Constraint constraint : constraints) {
                if (constraint.getType() != null && constraint.getType().equals(type))
                    return constraint;
            }
        }
        return null;
    }

    public static <T> boolean evaluate(Map<String, Field> fieldMap, Map<String, List<T>> submissionData, Constraint constraint) {
        if (constraint == null)
            return true;

        String constraintName = constraint.getName();
        String constraintValue = constraint.getValue();
        Pattern pattern = Pattern.compile(constraintValue);

        boolean isSatisfied = false;

        Field constraintField = fieldMap != null ? fieldMap.get(constraintName) : null;
        List<T> values = submissionData != null ? submissionData.get(constraintName) : null;

        // Evaluate whether this particular item is satisfied
        if (constraintField != null && (values == null || values.isEmpty())) {
            String defaultFieldValue = constraintField.getDefaultValue();
            isSatisfied = defaultFieldValue != null && pattern.matcher(defaultFieldValue).matches();
        } else {
            if (values != null) {
                for (T value : values) {
                    CharSequence actual = null;
                    if (value instanceof Value)
                        actual = ((Value)value).getValue();
                    isSatisfied = values != null && actual != null && pattern.matcher(actual).matches();
                    if (!isSatisfied)
                        break;
                }
            }
        }

        // If it is satisfied, then evaluate each of the 'and' constraints
        if (isSatisfied) {
            return checkAll(null, fieldMap, submissionData, constraint.getAnd());
        } else {
            if (constraint.getOr() != null && !constraint.getOr().isEmpty())
                return checkAny(null, fieldMap, submissionData, constraint.getOr());
        }


        return isSatisfied;
    }

    public static <T> boolean checkAll(String type, Map<String, Field> fieldMap, Map<String, List<T>> submissionData, List<Constraint> constraints) {
        if (constraints != null && !constraints.isEmpty()) {
            for (Constraint constraint : constraints) {
                if (type == null || constraint.getType() == null || constraint.getType().equals(type)) {
                    if (! evaluate(fieldMap, submissionData, constraint))
                        return false;
                }
            }
        }
        return true;
    }

    public static <T> boolean checkAny(String type, Map<String, Field> fieldMap, Map<String, List<T>> submissionData, List<Constraint> constraints) {
        if (constraints != null && !constraints.isEmpty()) {
            for (Constraint constraint : constraints) {
                if (type == null || constraint.getType() == null || constraint.getType().equals(type)) {
                    if (evaluate(fieldMap, submissionData, constraint))
                        return true;
                }
            }
            return false;
        }
        return true;
    }

    public static String fieldName(Field field, Map<String, List<Value>> data) {
        String fieldName = field.getName();

        if (fieldName == null) {
            if (field.getType() != null && field.getType().equalsIgnoreCase(FieldTag.CHECKBOX.getFieldType())) {
                List<Option> options = field.getOptions();
                if (options != null) {
                    for (Option option : options) {
                        if (StringUtils.isNotEmpty(option.getName()) && data.containsKey(option.getName()))
                            fieldName = option.getName();
                    }
                }
            }
        }
        return fieldName;
    }

    public static ManyMap<String, Value> filter(Collection<Field> fields, Map<String, List<Value>> original, DataFilter... dataFilters) {
        ManyMap<String, Value> map = new ManyMap<String, Value>();

        // Need to allow for adding default values -- in which case, the map may be empty
        Set<String> keys = new HashSet<String>();
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                if (StringUtils.isEmpty(field.getName()))
                    continue;
                keys.add(field.getName());
            }
        }

        if (original == null)
            original = Collections.emptyMap();

        if (!original.isEmpty()) {
            keys.addAll(original.keySet());
        }

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                List<Value> values = original.get(key);
                if (dataFilters != null) {
                    for (DataFilter dataFilter : dataFilters) {
                        values = dataFilter.filter(key, values);
                    }
                }
                if (values != null && !values.isEmpty())
                    map.put(key, values);
            }
        }

        return map;
    }

    public static <T> Map<String, List<T>> filter(Map<String, List<T>> data, Context context, boolean limitToEditable) {
        if (context == null)
            return null;

        if (context.isAllowAny()) {
            // If the context allows any data then all the data
            // will be included
            return data;
        } else {
            Map<String, Field> fields = context.getFields();

            if (fields == null || fields.isEmpty())
                return Collections.emptyMap();

            Map<String, List<T>> filtered = new ManyMap<String, T>();

            for (Field field : fields.values()) {
                if (StringUtils.isEmpty(field.getName()))
                    continue;

                if (limitToEditable && !field.isEditable())
                    continue;

                List<T> values = data.get(field.getName());
                if (values == null || values.isEmpty())
                    continue;

                filtered.put(field.getName(), values);
            }

            return filtered;
        }
    }

    public static Set<String> keywords(Value value) {
        Set<String> keywords = new HashSet<String>();
        if (value instanceof File) {
            File  file = File.class.cast(value);
            if (StringUtils.isNotEmpty(file.getName()))
                keywords.add(file.getName().toLowerCase());
        } else if (value instanceof User) {
            User user = User.class.cast(value);
            keywords.add(user.getVisibleId());
            if (StringUtils.isNotEmpty(user.getDisplayName())) {
                String lowerCase = user.getDisplayName().toLowerCase();
                keywords.add(lowerCase);
                String[] names = lowerCase.split("\\s+");
                if (names != null && names.length > 0)
                    keywords.addAll(Arrays.asList(names));
            }
        } else if (value instanceof Value) {
            String strValue = value.getValue();
            if (StringUtils.isNotEmpty(strValue)) {
                String resultString = strValue.toLowerCase().replaceAll("[^\\p{L}\\p{Nd}]", "");
                keywords.add(resultString);
            }
        }
        return keywords;
    }

    public static <T> Set<String> keywords(Map<String, List<T>> data) {
        Set<String> keywords = new HashSet<String>();

        for (Map.Entry<String, List<T>> entry : data.entrySet()) {
            List<T> values = entry.getValue();
            for (Object value : values) {
                if (value != null) {
                    if (value instanceof String)
                        value = new Value((String)value);

                    if (value instanceof Value)
                        keywords.addAll(Utility.keywords((Value)value));
                }
            }
        }

        return keywords;
    }

    public static Pageable pageable(Integer size, Integer pageNumber) {
        int selectedPage = pageNumber != null ? pageNumber.intValue() : 0;
        int selectedSize = size != null ? size.intValue() : 20;
        return new PageRequest(selectedPage, selectedSize);
    }

    public static <E, R extends ResourceSupport> PagedResources<R> pagedResources(Page<E> page, ResourceAssemblerSupport<E, R> assembler) throws ResourceNotFoundException {
        if (page == null)
            throw new ResourceNotFoundException("Search returned no results");

        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
        List<E> entities = page.getContent();
        List<R> resources = assembler.toResources(entities);

        PagedResources<R> pagedResources = new PagedResources<R>(resources, pageMetadata);
        pagedResources.add(linkTo(methodOn(DataController.class).search(20, 0)).withSelfRel());
        return pagedResources;
    }

    public static OptionResolver resolveConstraints(Field field, FieldTag fieldTag, Set<ValidationRule> rules, Registry registry) {
        String fieldName = field.getName();
        OptionResolver optionResolver = null;
        List<Constraint> constraints = field.getConstraints();
        Constraint onlyRequiredWhenConstraint = null;
        if (constraints != null && !constraints.isEmpty()) {
            for (Constraint constraint : constraints) {
                String type = constraint.getType();
                if (type == null)
                    continue;

                if (type.equals(Constants.ConstraintTypes.IS_ONLY_REQUIRED_WHEN)) {
                    rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.CONSTRAINED_REQUIRED)
                            .name(fieldName)
                            .constraint(constraint)
                            .build());
                    onlyRequiredWhenConstraint = constraint;
                } else if (type.equals(Constants.ConstraintTypes.IS_NUMERIC))
                    rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.NUMERIC).name(fieldName).build());
                else if (type.equals(Constants.ConstraintTypes.IS_EMAIL_ADDRESS))
                    rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.EMAIL).name(fieldName).build());
                else if (type.equals(Constants.ConstraintTypes.IS_ALL_VALUES_MATCH))
                    rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.VALUES_MATCH).name(fieldName).constraint(onlyRequiredWhenConstraint).build());
                else if (type.equals(Constants.ConstraintTypes.IS_LIMITED_TO))
                    optionResolver = registry.retrieve(Option.class, constraint.getName());
            }
        }

        if (onlyRequiredWhenConstraint == null && field.isRequired()) {
            if (fieldTag == FieldTag.FILE)
                rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.REQUIRED_IF_NO_PREVIOUS).name(fieldName).build());
            else
                rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.REQUIRED).name(fieldName).build());
        }

        if (field.getMaxInputs() > 1 || field.getMinInputs() > 1)
            rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.NUMBER_OF_INPUTS).name(fieldName).numberOfInputs(field.getMaxInputs(), field.getMinInputs()).constraint(onlyRequiredWhenConstraint).required(field.isRequired()).build());

        return optionResolver;
    }

    public static Set<ValidationRule> validationRules(Field field, Registry registry) {
        FieldTag fieldTag = FieldTag.getInstance(field.getType());

        String fieldName = field.getName();
        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        if (!field.isEditable())
            return rules;

        OptionResolver optionResolver = resolveConstraints(field, fieldTag, rules, registry);

        if (!FREEFORM_INPUT_TYPES.contains(fieldTag)) {
            List<Option> options = field.getOptions();

            if (optionResolver != null)
                options = optionResolver.getOptions();

            // If no options are stored, then assume that any option is valid
            if (options != null && !options.isEmpty()) {
                rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.LIMITED_OPTIONS)
                        .name(fieldName)
                        .options(options).build());
            }
        } else if (fieldTag == FieldTag.PERSON) {
            rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.VALID_USER).name(fieldName).build());
        } else {
            Pattern pattern = field.getPattern() != null ? Pattern.compile(field.getPattern()) : null;
            if (pattern != null)
                rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.PATTERN).name(fieldName).pattern(pattern).build());

            rules.add(new ValidationRule.Builder(ValidationRule.ValidationRuleType.VALUE_LENGTH).name(fieldName).valueLength(field.getMaxValueLength(), field.getMinValueLength()).build());
        }

        return rules;
    }

}

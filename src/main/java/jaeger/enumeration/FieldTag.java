package jaeger.enumeration;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author James Renfro
 */
public enum FieldTag {
    CHECKBOX(FieldTypes.CHECKBOX, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "checkbox"))),
    DATE(FieldTypes.DATE, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "date"))),
    DATETIME(FieldTypes.DATETIME, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "datetime"))),
    DATETIME_LOCAL(FieldTypes.DATETIME_LOCAL, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "datetime-local"))),
    EMAIL(FieldTypes.EMAIL, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "email"))),
    FILE(FieldTypes.FILE, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "file"))),
    HTML(FieldTypes.HTML, new FieldTagDefinition("div")),
    NUMBER(FieldTypes.NUMBER, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "number"))),
    PERSON(FieldTypes.PERSON, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "text"))),
    RADIO(FieldTypes.RADIO, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "radio"))),
    SELECT_MULTIPLE(FieldTypes.SELECT_MULTIPLE, new FieldTagDefinition("select", new FieldAttributeDefinition("multiple"))),
    SELECT_ONE(FieldTypes.SELECT_ONE, new FieldTagDefinition("select")),
    TEXT(FieldTypes.TEXT, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "text"))),
    TEXTAREA(FieldTypes.TEXTAREA, new FieldTagDefinition("textarea")),
    URL(FieldTypes.URL, new FieldTagDefinition("input", new FieldAttributeDefinition("type", "url")));

    private String fieldType;
    private FieldTagDefinition definition;

    private FieldTag(String fieldType, FieldTagDefinition definition) {
        this.fieldType = fieldType;
        this.definition = definition;
    }

    public String getTagName() {
        return definition.getTagName();
    }

    public String getFieldType() {
        return fieldType;
    }

    public Map<String, String> getAttributes() {
        return definition.getAttributes();
    }

    public static FieldTag getInstance(String type) {
        if (type == null)
            return TEXT;

        String uppercase = type.toUpperCase();
        uppercase = uppercase.replace('-', '_');
        return FieldTag.valueOf(uppercase);
    }

    public static FieldTag getInstance(String tagName, String type, String multiple) {
        if (tagName.equalsIgnoreCase("input")) {
            if (type.equalsIgnoreCase(FieldTypes.CHECKBOX))
                return CHECKBOX;
            if (type.equalsIgnoreCase(FieldTypes.DATE))
                return DATE;
            if (type.equalsIgnoreCase(FieldTypes.EMAIL))
                return EMAIL;
            if (type.equalsIgnoreCase(FieldTypes.FILE))
                return FILE;
            if (type.equalsIgnoreCase(FieldTypes.NUMBER))
                return NUMBER;
            if (type.equalsIgnoreCase(FieldTypes.PERSON))
                return PERSON;
            if (type.equalsIgnoreCase(FieldTypes.RADIO))
                return RADIO;
            if (type.equalsIgnoreCase(FieldTypes.TEXT))
                return TEXT;
            if (type.equalsIgnoreCase(FieldTypes.URL))
                return URL;
        } else if (tagName.equalsIgnoreCase("select")) {
            if (StringUtils.isNotEmpty(multiple))
                return SELECT_MULTIPLE;
            return SELECT_ONE;
        } else if (tagName.equalsIgnoreCase(FieldTypes.TEXTAREA))
            return TEXTAREA;

        return null;
    }

    public static class FieldTypes {
        public static final String CHECKBOX = "checkbox";
        public static final String DATE = "date";
        public static final String DATETIME = "datetime";
        public static final String DATETIME_LOCAL = "datetime-local";
        public static final String EMAIL = "email";
        public static final String FILE = "file";
        public static final String IFRAME = "iframe";
        public static final String HTML = "html";
        public static final String NUMBER = "number";
        public static final String PERSON = "person";
        public static final String RADIO = "radio";
        public static final String SELECT_ONE = "select-one";
        public static final String SELECT_MULTIPLE = "select-multiple";
        public static final String TEXT = "text";
        public static final String TEXTAREA = "textarea";
        public static final String URL = "url";
    }
    
}

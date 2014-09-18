package jaeger;

import jaeger.model.Constraint;
import jaeger.model.Field;
import jaeger.model.ManyMap;
import jaeger.model.Value;
import jaeger.test.ExampleFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UtilityTest {

    @Test
    public void testEvaluateDefaultValueTrue() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_REQUIRED_WHEN)
                .name(budgetNumber.getName())
                .value("^100000$")
                .build();

        boolean isSatisfied = Utility.evaluate(fieldMap, null, constraint);
        Assert.assertTrue(isSatisfied);
    }

    @Test
    public void testEvaluateDefaultValueFalse() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_REQUIRED_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .build();

        boolean isSatisfied = Utility.evaluate(fieldMap, null, constraint);
        Assert.assertFalse(isSatisfied);
    }

    @Test
    public void testEvaluateFormValueTrue() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_REQUIRED_WHEN)
                .name(budgetNumber.getName())
                .value("^200000$")
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("200000"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);
        Assert.assertTrue(isSatisfied);
    }

    @Test
    public void testEvaluateFormValueFalse() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_REQUIRED_WHEN)
                .name(budgetNumber.getName())
                .value("^200000$")
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("200001"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);
        Assert.assertFalse(isSatisfied);
    }

    @Test
    public void testAndConstraintFormValueOnlyFirstSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .and(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();


        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("200001"));
        data.putOne(actionType.getName(), new Value("demote"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);

        Assert.assertFalse(isSatisfied);
    }

    @Test
    public void testAndConstraintFormValueOnlySecondSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .and(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("200001$"));
        data.putOne(actionType.getName(), new Value("promote"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);
        Assert.assertFalse(isSatisfied);
    }

    @Test
    public void testAndConstraintFormValueBothSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .and(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("100001"));
        data.putOne(actionType.getName(), new Value("reprimand"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);

        Assert.assertTrue(isSatisfied);
    }

    @Test
    public void testAndConstraintFormValueNoneSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .and(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("200001"));
        data.putOne(actionType.getName(), new Value("demote"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);

        Assert.assertFalse(isSatisfied);
    }

    @Test
    public void testOrConstraintFormValueFirstSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .or(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("100001"));
        data.putOne(actionType.getName(), new Value("demote"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);


        Assert.assertTrue(isSatisfied);
    }

    @Test
    public void testOrConstraintFormValueSecondSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .or(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("200001$"));
        data.putOne(actionType.getName(), new Value("promote"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);

        Assert.assertTrue(isSatisfied);
    }

    @Test
    public void testOrConstraintFormValueBothSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .or(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();


        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("100001"));
        data.putOne(actionType.getName(), new Value("reprimand"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);
        Assert.assertTrue(isSatisfied);
    }

    @Test
    public void testOrConstraintFormValueNoneSatisfied() throws Exception {
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        Field actionType = ExampleFactory.actionTypeField();
        fieldMap.put(actionType.getName(), actionType);

        Field budgetNumber = ExampleFactory.budgetNumberField();
        fieldMap.put(budgetNumber.getName(), budgetNumber);

        Constraint constraint = new Constraint.Builder()
                .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                .name(budgetNumber.getName())
                .value("^100001$")
                .or(new Constraint.Builder()
                        .type(Constants.ConstraintTypes.IS_ONLY_VISIBLE_WHEN)
                        .name(actionType.getName())
                        .value("^(reprimand|promote)$")
                        .build())
                .build();

        ManyMap<String, Value> data = new ManyMap<String, Value>();
        data.putOne(budgetNumber.getName(), new Value("200001"));
        data.putOne(actionType.getName(), new Value("demote"));

        boolean isSatisfied = Utility.evaluate(fieldMap, data, constraint);
        Assert.assertFalse(isSatisfied);
    }


}
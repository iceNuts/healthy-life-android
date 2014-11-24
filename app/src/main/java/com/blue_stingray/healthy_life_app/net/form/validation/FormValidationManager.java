package com.blue_stingray.healthy_life_app.net.form.validation;

import android.view.View;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Manages validations on a form level. Validations will only show up after the form is validated or the user focuses
 * and then unfocuses a TextView
 */
public class FormValidationManager {
    private Map<TextView, FieldInfo> fieldMap = Maps.newHashMap();
    private Map<TextView, List<TextView>> invertedDependencies = Maps.newHashMap();

    /**
     * Validates all fields in a form
     */
    public void validateForm() {
        for (Map.Entry<TextView, FieldInfo> entry : fieldMap.entrySet()) {
            if (!entry.getValue().validated) {
                validateField(entry.getKey());
            }
        }
    }

    /**
     * Adds a field to be validated
     * @param view the TextView to be validated
     * @param rule the ValidationRule used to validate the TextView
     * @param dependencies Dependant TextViews that should be reevaluated after this one
     */
    public void addField(TextView view, ValidationRule rule, TextView... dependencies) {
        fieldMap.put(view, new FieldInfo(rule));
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean inFocus) {
                if (inFocus) {
                    return;
                }
                validateField((TextView)view);
            }
        });

        for (TextView dependency : dependencies) {
            if (invertedDependencies.containsKey(dependency)) {
                invertedDependencies.get(dependency).add(view);
            } else {
                invertedDependencies.put(dependency, Lists.newArrayList(view));
            }
        }
    }

    /**
     * Validates the form and returns whether it is valid
     * @return whether the form is valid
     */
    public boolean isFormValid() {
        validateForm();
        for (FieldInfo fieldInfo : fieldMap.values()) {
            if (!fieldInfo.isValid) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates a TextView that has been added
     * @param field the TextView
     */
    private void validateField(TextView field) {
        FieldInfo fieldInfo = fieldMap.get(field);

        fieldInfo.isValid = fieldInfo.rule.isValid(field.getText());
        fieldInfo.validated = true;

        if (fieldInfo.isValid) {
            field.setError(null);
        } else {
            field.setError(fieldInfo.rule.getError(field.getText()));
        }

        List<TextView> affected = invertedDependencies.get(field);
        if (affected != null) {
            for (TextView view : affected) {
                if (fieldMap.get(view).validated) {
                    validateField(view);
                }
            }
        }
    }

    /**
     * Structure containing validation information about a field
     */
    private static class FieldInfo {
        public boolean validated;
        public ValidationRule rule;
        public boolean isValid;

        public FieldInfo(ValidationRule rule) {
            this.rule = rule;
        }
    }
}

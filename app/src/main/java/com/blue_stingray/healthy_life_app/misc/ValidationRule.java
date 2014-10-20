package com.blue_stingray.healthy_life_app.misc;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;
import com.blue_stingray.healthy_life_app.R;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Rules for form validation
 */
public class ValidationRule {

    private List<ConstraintWithMessage> constraints = Lists.newArrayList();

    public boolean isValid(CharSequence toValidate) {
        return getError(toValidate) == null;
    }

    public ValidationRule addConstraint(ValidationConstraint constraint, String message) {
        constraints.add(new ConstraintWithMessage(constraint, message));
        return this;
    }

    public String getError(CharSequence toValidate) {
        for (ConstraintWithMessage constraintWithMessage : constraints) {
            if (!constraintWithMessage.constraint.isValid(toValidate)) {
                return constraintWithMessage.message;
            }
        }
        return null;
    }

    public interface ValidationConstraint {
        public boolean isValid(CharSequence chars);
    }

    private class ConstraintWithMessage {
        public final ValidationConstraint constraint;
        public final String message;

        public ConstraintWithMessage(ValidationConstraint constraint, String message) {
            this.constraint = constraint;
            this.message = message;
        }
    }

    public static ValidationRule newPasswordValidationRule(Context ctx) {
        return new ValidationRule()
                .addConstraint(
                        new ValidationRule.ValidationConstraint() {
                            @Override
                            public boolean isValid(CharSequence chars) {
                                return chars.length() != 0;
                            }
                        }, ctx.getString(R.string.missing_password)
                )
                .addConstraint(
                        new ValidationRule.ValidationConstraint() {
                            @Override
                            public boolean isValid(CharSequence chars) {
                                return chars.length() >= 6;
                            }
                        }, ctx.getString(R.string.short_password)
                );
    }

    public static ValidationRule newConfirmPasswordValidationRule(Context ctx, final EditText toConfirm) {
        return new ValidationRule()
                .addConstraint(new ValidationConstraint() {
                    @Override
                    public boolean isValid(CharSequence chars) {
                        return toConfirm.getText().toString().equals(chars.toString());
                    }
                }, ctx.getString(R.string.non_matching_password));
    }

    public static ValidationRule newEmailValidationRule(Context ctx) {
        return new ValidationRule()
                .addConstraint(
                        new ValidationRule.ValidationConstraint() {
                            @Override
                            public boolean isValid(CharSequence chars) {
                                return chars.length() != 0;
                            }
                        }, ctx.getString(R.string.missing_email)
                )
                .addConstraint(
                        new ValidationRule.ValidationConstraint() {
                            @Override
                            public boolean isValid(CharSequence chars) {
                                return Patterns.EMAIL_ADDRESS.matcher(chars).matches();
                            }
                        }, ctx.getString(R.string.invalid_email)
                );
    }
}

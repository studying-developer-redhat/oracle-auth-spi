package com.redhat.rhsso.spi.validator.impl;

import com.redhat.rhsso.spi.config.UserFederationConfig;
import com.redhat.rhsso.spi.validator.AbstractPasswordValidator;

public class PasswordValidator extends AbstractPasswordValidator<String> {

    private UserFederationConfig configmap;

    public PasswordValidator(String o, UserFederationConfig configmap) {
        super(o);
        this.configmap = configmap;
    }

    @Override
    public boolean isRespectingRegex() {
        return getEntity().matches(getRegex());
    }

    @Override
    public int getMinimumLength() {
        return this.configmap.passwordMinLength();
    }

    @Override
    public int getMaximumLength() {
        return this.configmap.passwordMaxLength();
    }

    @Override
    public int getMaximumRepeatedChars() {
        return this.configmap.passwordMaxRepeatedCharsLength();
    }

    /**
     * This regular expression match can be used for validating strong passwords.
     * It expects at least:
     * 1 small-case letter.
     * 1 upper-case letter.
     * 1 digit.
     * 1 special character.
     * <br/>
     * The length should be between {minimum}-{maximum} characters.
     * The sequence of the characters is not important.
     * It must not contains whitespace characters.
     * It must not contains more than 3 repeated characters.
     * It must not contains: &amp; &gt; &lt; &quot;.
     * @return String
     */
    @Override
    public String getRegex() {
        return "(?=^.{"+getMinimumLength()+","+getMaximumLength()+"}$)(?!.*(.)\\1{"+getMaximumRepeatedChars()+"})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$$%^*()_+}{:;'?/.,])(?!.*\\s).*$";
    }

    @Override
    public String trim() {
        return null;
    }

    @Override
    public boolean isValid() {
        if (getEntity().isEmpty()) {
            return false;
        }

        if (!isRespectingRegex()) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "UserPassword";
    }
}

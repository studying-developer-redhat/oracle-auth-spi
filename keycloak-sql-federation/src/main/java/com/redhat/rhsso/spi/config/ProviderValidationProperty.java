package com.redhat.rhsso.spi.config;

public class ProviderValidationProperty {

    private String name;
    private String regex;
    private Boolean nullable;
    private String message;

    public ProviderValidationProperty(String name, String message, Boolean nullable) {
        this.name = name;
        this.message = message;
        this.nullable = nullable;
    }

    public ProviderValidationProperty(String name, String message, String regex, Boolean nullable) {
        this.name = name;
        this.message = message;
        this.regex = regex;
        this.nullable = nullable;
    }

    public boolean isRespectingRegex(String value) {
        return value.matches(getRegex());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Boolean allowNull() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
}

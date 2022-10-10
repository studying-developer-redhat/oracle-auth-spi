package com.redhat.rhsso.spi.validator;

public abstract class AbstractPasswordValidator<T> extends AbstractValidator<T> {

    public AbstractPasswordValidator(T o) {
        super(o);
    }

    public abstract boolean isRespectingRegex();

    public abstract int getMinimumLength();

    public abstract int getMaximumLength();

    public abstract int getMaximumRepeatedChars();

    public abstract String getRegex();

    public abstract T trim();

}

package com.redhat.rhsso.spi.validator;

public abstract class AbstractValidator<T> {

    private T entity;

    public AbstractValidator(T t) {
        this.entity = t;
    }

    public abstract boolean isValid();

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public abstract String getName();

}

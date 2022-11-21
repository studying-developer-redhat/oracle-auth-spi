package com.redhat.rhsso.spi.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
public class UserRequestCreateDTO implements Serializable {
    private String username;
}

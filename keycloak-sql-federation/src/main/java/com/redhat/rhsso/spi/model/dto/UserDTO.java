package com.redhat.rhsso.spi.model.dto;

import com.redhat.rhsso.spi.model.request.UserRequestUpdateDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
public class UserDTO implements Serializable {

    private Long id;
    private PersonDTO person;
    private String username;
    private String password;
    private String email;

    public UserDTO() {}

    public UserDTO(final UserRequestUpdateDTO updateDTO) {
        this.id = updateDTO.getId();
        this.person = updateDTO.getPerson();
        this.username = updateDTO.getUsername();
        this.password = updateDTO.getPassword();
        this.email = updateDTO.getEmail();
    }
}

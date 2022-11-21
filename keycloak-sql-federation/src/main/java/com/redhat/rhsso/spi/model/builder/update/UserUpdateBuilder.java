package com.redhat.rhsso.spi.model.builder.update;

import com.redhat.rhsso.spi.model.dto.UserDTO;
import com.redhat.rhsso.spi.model.entity.Person;
import com.redhat.rhsso.spi.model.entity.User;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class UserUpdateBuilder {

    @EJB
    private PersonUpdateBuilder personUpdateBuilder;

    public User build(final UserDTO userDTO) {
        final Person person = personUpdateBuilder.build(userDTO.getPerson());
        return User.builder()
                .id(userDTO.getId())
                .person(person)
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .build();
    }
}

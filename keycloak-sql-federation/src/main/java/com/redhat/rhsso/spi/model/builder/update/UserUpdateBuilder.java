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
        User user = new User();
        user.setId(userDTO.getId());
        user.setPerson(person);
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}

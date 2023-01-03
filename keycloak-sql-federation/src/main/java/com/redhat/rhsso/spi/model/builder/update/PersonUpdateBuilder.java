package com.redhat.rhsso.spi.model.builder.update;

import com.redhat.rhsso.spi.model.dto.PersonDTO;
import com.redhat.rhsso.spi.model.entity.Person;

import javax.ejb.Stateless;

@Stateless
public class PersonUpdateBuilder {

    public Person build(final PersonDTO personDTO) {
        Person person = new Person();
        person.setId(personDTO.getId());
        person.setName(personDTO.getName());
        person.setMiddle(personDTO.getMiddle());
        person.setFamily(personDTO.getFamily());
        person.setIssn(personDTO.getIssn());
        person.setStatus(personDTO.getStatus());
        person.setCreation(personDTO.getCreation());
        return person;
    }
}

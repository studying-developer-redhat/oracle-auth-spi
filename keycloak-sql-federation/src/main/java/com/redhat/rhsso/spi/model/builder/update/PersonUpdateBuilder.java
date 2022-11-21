package com.redhat.rhsso.spi.model.builder.update;

import com.redhat.rhsso.spi.model.dto.PersonDTO;
import com.redhat.rhsso.spi.model.entity.Person;

import javax.ejb.Stateless;

@Stateless
public class PersonUpdateBuilder {

    public Person build(final PersonDTO personDTO) {
        return Person.builder()
                .id(personDTO.getId())
                .name(personDTO.getName())
                .middle(personDTO.getMiddle())
                .family(personDTO.getFamily())
                .issn(personDTO.getIssn())
                .status(personDTO.getStatus())
                .creation(personDTO.getCreation())
                .build();
    }
}

package com.redhat.rhsso.spi.model.builder.create;

import com.redhat.rhsso.spi.model.entity.Person;
import javax.ejb.Stateless;
import java.sql.Date;
import java.time.LocalDate;

@Stateless
public class PersonCreateBuilder {

    public Person build(final String username) {

        Person person = new Person();
        person.setName(username);
        person.setMiddle("-");
        person.setFamily("-");
        person.setIssn("-");
        person.setStatus("1");
        person.setCreation(Date.valueOf(LocalDate.now()));

        return person;

    }
}

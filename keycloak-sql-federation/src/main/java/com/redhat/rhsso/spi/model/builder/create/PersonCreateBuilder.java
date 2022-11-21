package com.redhat.rhsso.spi.model.builder.create;

import com.redhat.rhsso.spi.model.entity.Person;
import javax.ejb.Stateless;
import java.sql.Date;
import java.time.LocalDate;

@Stateless
public class PersonCreateBuilder {

    public Person build(final String username) {
        return Person.builder()
                .name(username)
                .middle("-")
                .family("-")
                .issn("-")
                .status("1")
                .creation(Date.valueOf(LocalDate.now()))
                .build();
    }
}

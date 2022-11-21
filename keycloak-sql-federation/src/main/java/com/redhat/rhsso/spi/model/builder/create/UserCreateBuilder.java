package com.redhat.rhsso.spi.model.builder.create;

import com.redhat.rhsso.spi.helper.CredentialHelper;
import com.redhat.rhsso.spi.model.entity.Person;
import com.redhat.rhsso.spi.model.entity.User;
import javax.ejb.Stateless;

@Stateless
public class UserCreateBuilder {

    public User build(final Person person) {
        return User.builder()
                .id(person.getId())
                .person(person)
                .username(person.getName())
                .password(getTemporaryCredential().generate(20))
                .email("-")
                .build();
    }


    private CredentialHelper getTemporaryCredential() {
        // User will not have direct access upon registration.
        // An update-password required action will be set for new users.
        return new CredentialHelper.PasswordGeneratorBuilder()
                .useDigits(true)
                .useLower(true)
                .useUpper(true)
                .usePunctuation(true)
                .build();
    }
}

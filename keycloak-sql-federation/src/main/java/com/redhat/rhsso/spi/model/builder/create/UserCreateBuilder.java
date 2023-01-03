package com.redhat.rhsso.spi.model.builder.create;

import com.redhat.rhsso.spi.helper.CredentialHelper;
import com.redhat.rhsso.spi.model.entity.Person;
import com.redhat.rhsso.spi.model.entity.User;
import javax.ejb.Stateless;

@Stateless
public class UserCreateBuilder {

    public User build(final Person person) {

        User user = new User();
        user.setId(person.getId());
        user.setPerson(person);
        user.setUsername(person.getName());
        user.setPassword(getTemporaryCredential().generate(20));
        user.setEmail("-");

        return user;
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

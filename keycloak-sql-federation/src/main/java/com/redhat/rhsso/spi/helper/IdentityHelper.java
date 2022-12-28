package com.redhat.rhsso.spi.helper;

import com.redhat.rhsso.spi.config.UserFederationConfig;
import com.redhat.rhsso.spi.model.entity.User;
import com.redhat.rhsso.spi.validator.impl.PasswordValidator;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class IdentityHelper {

    public static boolean isValidUsername(EntityManager em, String username) {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_USERNAME, User.class);
        query.setParameter("username", username);
        List<User> result = query.getResultList();
        return result.isEmpty();
    }

    public static boolean isValidPassword(String pw, UserFederationConfig configmap) {
        return new PasswordValidator(pw, configmap).isValid();
    }

}

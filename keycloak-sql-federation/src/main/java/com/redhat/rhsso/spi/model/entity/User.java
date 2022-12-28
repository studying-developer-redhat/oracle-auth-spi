package com.redhat.rhsso.spi.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@ToString
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@NamedQueries({
        @NamedQuery(name = User.FIND_BY_USERNAME, query = "select u from User u where lower(u.username) = lower(:username)"),
        @NamedQuery(name = User.FIND_BY_EMAIL, query = "select u from User u where lower(u.email) = lower(:email)"),
        @NamedQuery(name = User.GET_USER_COUNT, query = "select count(u.id) from User u"),
        @NamedQuery(name = User.FIND_ALL, query = "select u from User u"),
        @NamedQuery(name = User.FIND_BY_USERNAME_OR_EMAIL, query = "select u from User u where ( lower(u.username) like :search or lower(u.email) like :search ) order by u.username")
})
@Entity
@Table(name = "USERS", schema = "OT")
public class User implements Serializable {

    public static final String FIND_BY_USERNAME = "User.findByUsername";
    public static final String FIND_BY_EMAIL = "User.findByEmail";
    public static final String GET_USER_COUNT = "User.getUserCount";
    public static final String FIND_ALL = "User.findAll";
    public static final String FIND_BY_USERNAME_OR_EMAIL = "User.findByUsernameOrEmail";

    @Id
    @Column(name = "ID_PERSON", nullable = false, unique = true)
    private Long id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_PERSON")
    private Person person;

    @Basic
    @Column(name = "DS_USERNAME", nullable = false, length = 25)
    private String username;

    @Basic
    @Column(name = "DS_PASSWORD", nullable = false, length = 100)
    private String password;

    @Basic
    @Column(name = "DS_EMAIL", nullable = false, length = 255)
    private String email;

}

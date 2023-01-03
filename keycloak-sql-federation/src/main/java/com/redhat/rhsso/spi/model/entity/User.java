package com.redhat.rhsso.spi.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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

    public User() {}

    public User(Long id, Person person, String username, String password, String email) {
        this.id = id;
        this.person = person;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", person=" + person +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

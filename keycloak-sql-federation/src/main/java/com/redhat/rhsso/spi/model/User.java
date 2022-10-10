package com.redhat.rhsso.spi.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name="getUserByUsername", query="select u from Users u where lower(u.username) = lower(:username)"),
        @NamedQuery(name="getUserByEmail", query="select u from Users u where lower(u.email) = lower(:email)"),
        @NamedQuery(name="getUserCount", query="select count(u.id) from Users u"),
        @NamedQuery(name="getAllUsers", query="select u from Users u"),
        @NamedQuery(name="searchForUser", query="select u from Users u where ( lower(u.username) like :search or lower(u.email) like :search ) order by u.username")
})
@Entity(name = "User")
@Table(name = "USERS", schema = "OT", catalog = "")
public class User implements Serializable {

    public User() { }

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId()
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

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Person getPerson() {
        return person;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setPerson(Person person) {
        this.person = person;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId().equals(user.getId()) &&
                Objects.equals(getUsername(), user.getUsername()) &&
                getPassword().equals(user.getPassword()) &&
                getEmail().equals(user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getPassword(), getEmail());
    }
}

package com.redhat.rhsso.spi.model.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = Person.GET_PERSON_BY_ISSN, query = "select p from Person p where p.id = :issn")
})
@Entity
@Table(name = "PERSON", schema = "OT")
public class Person implements Serializable {

    public static final String GET_PERSON_BY_ISSN = "Person.getPersonByISSN";

    @Id
    @Column(name = "ID_PERSON", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_person_generator")
    @SequenceGenerator(sequenceName = "OT.SEQ_PERSON", initialValue = 1, allocationSize = 10, schema = "OT", name = "seq_person_generator")
    private Long id;

    @Column(name = "NM_PERSON", nullable = false, length = 150)
    private String name;

    @Basic
    @Column(name = "NM_MIDDLE", nullable = true, length = 100)
    private String middle;

    @Basic
    @Column(name = "NM_FAMILY", nullable = false, length = 50)
    private String family;

    @Column(name = "ID_ISSN", nullable = false, length = 50)
    private String issn;

    @Column(name = "DT_CREATED", nullable = false)
    private Date creation;

    @Column(name = "ST_PERSON", nullable = true, length = 1)
    private String status;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_PERSON")
    private User user;

    public Person() { }

    public Person(Long id, String name, String middle, String family, String issn, Date creation, String status, User user) {
        this.id = id;
        this.name = name;
        this.middle = middle;
        this.family = family;
        this.issn = issn;
        this.creation = creation;
        this.status = status;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", middle='" + middle + '\'' +
                ", family='" + family + '\'' +
                ", issn='" + issn + '\'' +
                ", creation=" + creation +
                ", status='" + status + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id.equals(person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

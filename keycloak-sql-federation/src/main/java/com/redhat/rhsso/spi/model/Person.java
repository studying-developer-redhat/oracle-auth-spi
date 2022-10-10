package com.redhat.rhsso.spi.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name="getPersonByISSN", query="select p from Person p where p.id = :issn")
})
@Entity(name = "Person")
@Table(name = "PERSON", schema = "OT", catalog = "")
public class Person implements Serializable {

    public Person() { }

    @Id
    @Column(name = "ID_PERSON", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_person_generator")
    @SequenceGenerator(sequenceName = "OT.SEQ_PERSON", initialValue = 1, allocationSize = 100, schema = "OT", name = "seq_person_generator")
    private Long id;

    @Basic
    @Column(name = "NM_PERSON", nullable = false, length = 150)
    private String name;

    @Basic
    @Column(name = "NM_MIDDLE", nullable = true, length = 100)
    private String middle;

    @Basic
    @Column(name = "NM_FAMILY", nullable = false, length = 50)
    private String family;

    @Basic
    @Column(name = "ID_ISSN", nullable = false, length = 50)
    // National Identification for Person
    private String issn;

    @Basic
    @Column(name = "DT_CREATED", nullable = false)
    private Date creation;

    @Basic
    @Column(name = "ST_PERSON", nullable = true, length = 1)
    private String status;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMiddle() {
        return middle;
    }

    public String getFamily() {
        return family;
    }

    public String getIssn() {
        return issn;
    }

    public Date getCreation() {
        return creation;
    }

    public String getStatus() {
        return status;
    }

    public Person setId(Long id) {
        this.id = id;
        return this;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public Person setMiddle(String middle) {
        this.middle = middle;
        return this;
    }

    public Person setFamily(String family) {
        this.family = family;
        return this;
    }

    public Person setIssn(String issn) {
        this.issn = issn;
        return this;
    }

    public Person setCreation(Date creation) {
        this.creation = creation;
        return this;
    }

    public Person setStatus(String status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getId().equals(person.getId()) &&
                getName().equals(person.getName()) &&
                Objects.equals(getMiddle(), person.getMiddle()) &&
                getFamily().equals(person.getFamily()) &&
                getIssn().equals(person.getIssn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getMiddle(), getFamily(), getIssn());
    }
}

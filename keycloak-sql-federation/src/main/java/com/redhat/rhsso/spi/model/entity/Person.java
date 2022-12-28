package com.redhat.rhsso.spi.model.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@ToString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
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

}

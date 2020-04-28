package com.postnow.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Past;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class UserAdditionalData implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Length(min = 3, max = 20)
    @Column(nullable = false)
    private String firstName;

    @Length(min = 3, max = 30)
    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Past
    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(unique = true, nullable = true, length = 9)
    private String phoneNumber;

    @Length(min = 4, max = 35)
    @Column(unique = false, nullable = true)
    private String homeTown;

    @Column(unique = false, nullable = true)
    private boolean inRelationship;

    public void setBirthDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.birthDate = LocalDate.parse(date, formatter);
    }
}

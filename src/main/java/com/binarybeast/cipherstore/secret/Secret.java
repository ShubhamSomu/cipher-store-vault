package com.binarybeast.cipherstore.secret;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Secret {
    @JsonIgnore
    @Id
    @GeneratedValue
    Integer id;

    @NotNull
    @Column
    private String mek;

    @NotNull
    @Column
    private String dek;
}
package com.binarybeast.cipherstore.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
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
package com.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class Properties {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long     id;

    @Column(length = 4096)
    private String  name;

    @Column(length = 4096)
    private String  value;
}

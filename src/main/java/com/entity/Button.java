package com.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class Button {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = false)
    private long     id;

    @Column(length = 4096)
    private String  name;

    @Column
    private Integer commandId;

    private int     langId;

}

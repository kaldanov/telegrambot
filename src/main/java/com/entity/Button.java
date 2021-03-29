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

    @Column(length = 300)
    private String  name;

    @Column(columnDefinition = "int default 0")
    private Integer commandId;

    @Column(length = 4096)
    private String  url;

    private boolean requestContact;


    private int     langId;

}

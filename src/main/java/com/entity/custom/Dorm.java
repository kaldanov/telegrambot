package com.entity.custom;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table
public class Dorm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(length = 4096)
    private String fullName;

    private boolean gender;

    private Date birthDate;

    private String nationality;

    @Column(length = 12)
    private String iin;

    @Column(length = 4096)
    private String address;

    @Column(length = 50)
    private String phone;

    @Column(length = 50)
    private String phoneParent;

    private Integer educationLangId;

    @Column(length = 4096)
    private String educationCourse;

    @Column(length = 4096)
    private String educationPeriod;

    @Column(length = 4096)
    private int educationSchool;

    @Column(length = 4096)
    private int speciality;

    private int typeDorm;

    private int roomId;

    private String socialGroup;

    private String socialGroupUrl;

    private String cardUrl;


    public Dorm() {
    }


}

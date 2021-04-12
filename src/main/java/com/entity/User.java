package com.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long     id;

    private long    chatId;

    @Column(length = 50)
    private String  phone;

    @Column(length = 200)
    private String  fullName;

    @Column(length = 4096)
    private String email;

    public User() {
    }


}

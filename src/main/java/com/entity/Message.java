package com.entity;

import com.enums.FileType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table
public class Message {

    @Id
    @Column(unique = false)
    private long     id;

    @Column(length = 8096)
    private String  name;



    private int     langId;


}

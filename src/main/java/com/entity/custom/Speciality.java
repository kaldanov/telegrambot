package com.entity.custom;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table
public class Speciality {

    @Id
    @Column(unique = false)
    private long id;

    @Column(length = 4096)
    private String name;

    private int type;

    private int langId;

    private Integer schoolType;


}

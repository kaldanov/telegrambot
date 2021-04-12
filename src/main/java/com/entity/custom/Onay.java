package com.entity.custom;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table
public class Onay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date date;

    @Column(length = 50)
    private String phone;

    @Column(length = 4096)
    private String fullName;

    @Column(length = 4096)
    private String cardId;

    @Column(length = 4096)
    private String iin;

    private Date dateIssue;

    private Date dateEnd;

    @Column(length = 4096)
    private String issuedBy;

    @Column(length = 4096)
    private String cardUrl;

    @Column(length = 4096)
    private String photoUrl;

    @Column(columnDefinition = "varchar(255) default 'Не получал'")
    private String gotOnay;

    public Onay() {
    }


}

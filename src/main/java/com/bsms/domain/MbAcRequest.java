package com.bsms.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "MB_AC_Request")
@Data
public class MbAcRequest {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private String id;

    private String msisdn;

    private String message;

    private String dateReceived;

    private String status;

    private String remark;
}

package com.bsms.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MB_LIMIT_VIEW")
@Data
public class MbLimit {

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "customer_type")
    private Integer customerType;
    @Column(name = "trx_type")
    private Integer trxType;
    @Column(name = "trx_amount_limit")
    private String trxAmountLimit;
    @Column(name = "daily_amount_limit")
    private String dailyAmountLimit;
    @Column(name = "enabled")
    private String enabled;
    @Column(name = "last_access_date")
    private String lastAccessDate;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "description")
    private String description;


}

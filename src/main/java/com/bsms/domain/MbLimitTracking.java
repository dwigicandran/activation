package com.bsms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MB_LIMITTRACKING_VIEW")
@Data
public class MbLimitTracking {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "trx_type")
    private Integer trxType;
    @Column(name = "last_trx_date")
    private Date lastTrxDate;
    @Column(name = "total_amount")
    private String totalAmount;

}

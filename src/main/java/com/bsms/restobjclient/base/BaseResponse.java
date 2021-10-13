package com.bsms.restobjclient.base;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BaseResponse implements Serializable {
    private String transactionId;
    private String correlationId;
    private String amount;
    private String responseCode;
    private String responseMessage;
    private String stan;
    private Object responseContent;
    private Object content;
    private String rc;
}

package com.bsms.restobj;


import com.bsms.restobjclient.authentication.ActivationResp;
import com.bsms.restobjclient.authentication.PINKeyResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MbApiResp implements Serializable {

    private static final long serialVersionUID = 3205059529639297899L;

    private String txId;
    private String responseTime;
    private String clientId;
    private String coreJournal;
    private String timestamp;
    private String message;
    private String path;
    private String status;
    private String error;
    private String correlationId;
    private String branchId;
    private String tellerId;
    private String clearZpk;
    private String transactionId;
    private String responseCode;
    private String response;
    private String channelType;
    private String channelId;
    private String traceNum;
    private String token;
    private String responseDescription;
    private String responseMessage;
    private String amount;

    private Long customerId;

    private Object content;
    private Object responseContent;
    private MbApiStatusResp respStatus;
    private PINKeyResp pinKeyResp;
    private ActivationResp activationResp;
    
    private String code;
    private String respTime;

    @JsonInclude(JsonInclude.Include.NON_NULL) private String trxId;
    @JsonInclude(JsonInclude.Include.NON_NULL) private String trxDateTime;

    //setting
    private String rc;

    public MbApiResp() {

    }

    public MbApiResp(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

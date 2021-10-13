package com.bsms.restobjclient.authentication;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class PINKeyDispResp implements MbApiContentResp, Serializable {

	private String responseCode;
	private String responseDescription;
	private String correlationId;
	private String transactionId;
	private String respTime;
	private String clearZpk;
	private String response;
	
}

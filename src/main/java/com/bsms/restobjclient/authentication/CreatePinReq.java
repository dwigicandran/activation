package com.bsms.restobjclient.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class CreatePinReq implements Serializable {
	
	private static final long serialVersionUID = -1957143381189203316L;
	
	private String zpk;
	private String pin;
	@JsonProperty("card_number")
	private String cardNumber;

}

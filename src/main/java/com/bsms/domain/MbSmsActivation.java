package com.bsms.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "MB_smsactivation")
@Data
public class MbSmsActivation {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	
	private String msisdn;
	
	private String message;
	
	private String dateReceived;
	
	private String isverified;
	
	private String dateVerified;
	
}

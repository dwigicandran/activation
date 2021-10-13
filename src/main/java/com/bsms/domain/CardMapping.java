package com.bsms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "cardmapping")
public class CardMapping {

	@Id
	@JsonProperty("ID")
	private Long ID;
	
	@Column(name = "pinoffset")
	private String pinoffset;
	
	@Column(name = "accountnumber")
	private String accountnumber;
	
	@Column(name ="cardnumber")
	private String cardnumber;
	
	@Column(name ="customerid")
	private Long customerid;
	
	@Column(name ="accounttype")
	private String accounttype;
	
	@Column(name ="branchcode")
	private String branchcode;
	
	
}

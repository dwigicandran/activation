package com.bsms.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "errormessage")
@Data
public class ErrorMessage {

	@Id
	private String code;
	
	private String language;
	private String description;

	
}

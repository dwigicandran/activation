package com.bsms.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "Notif_Cglist")
@Data
public class NotifCGList {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long idCgl;
	
	@Column(name = "MSISDN")
	private String msisdn;
	@Column(name = "ID_CG")
	private Long idCg;

	
}

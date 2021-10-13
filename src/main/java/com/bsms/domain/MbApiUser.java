package com.bsms.domain;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.Principal;

@Document(collection = "mb_user")
@Data
public class MbApiUser implements Principal, Serializable {

	@Id
    private String id;

    @NotBlank(message = "Username must be not blank.")
    private String username;

    @NotBlank(message = "Password must be not blank.")
    private String password;

    @NotBlank(message = "Batch token must be not blank.")
    @Size(min=8, max=17)
    private String batchToken;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

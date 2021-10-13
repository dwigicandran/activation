package com.bsms.repository;

import com.bsms.domain.ErrorMessage;
import org.springframework.data.repository.CrudRepository;

public interface ErrormsgRepository extends CrudRepository<ErrorMessage, String> {

	ErrorMessage findByCodeAndLanguage(String responseCode, String Language);
	
}

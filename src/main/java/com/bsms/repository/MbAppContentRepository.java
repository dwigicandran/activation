package com.bsms.repository;

import com.bsms.domain.MbAppContent;
import org.springframework.data.repository.CrudRepository;

public interface MbAppContentRepository extends CrudRepository<MbAppContent, String> {

	MbAppContent findByLangIdAndLanguage(String langId, String language);
	
}

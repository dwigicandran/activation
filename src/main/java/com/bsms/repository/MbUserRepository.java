package com.bsms.repository;

import com.bsms.domain.MbApiUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MbUserRepository extends MongoRepository<MbApiUser, String> {
	
	public MbApiUser findOneByUsernameAndPassword(String username, String password);
	
	public MbApiUser findOneByUsername(String username);

}

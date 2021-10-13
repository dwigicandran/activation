package com.bsms.repository;

import com.bsms.domain.NotifCGList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface NotifcglistRepository extends CrudRepository<NotifCGList, Long> {

	NotifCGList findByMsisdnAndIdCg(String noHp, Long flag);
	
	long countByMsisdn(String noHp);
	
}

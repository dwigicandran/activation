package com.bsms.repository;


import com.bsms.domain.MbAcRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MbAcRequestRepository extends CrudRepository<MbAcRequest, String> {

    @Modifying(clearAutomatically = true)
    @Query(value = "insert into mb_ac_request (msisdn, message,status,remark) values (:msisdn, :message, :status, :remark)", nativeQuery = true)
    void saveMbAc(@Param("msisdn") String msisdn, @Param("message") String message, @Param("status") String status, @Param("remark") String remark);

}

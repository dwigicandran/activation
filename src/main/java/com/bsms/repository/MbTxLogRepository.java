package com.bsms.repository;

import com.bsms.domain.MbApiTxLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MbTxLogRepository extends MongoRepository<MbApiTxLog, String> {
    Optional<MbApiTxLog> findById(String id);
}

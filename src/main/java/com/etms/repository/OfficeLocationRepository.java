package com.etms.repository;

import com.etms.entity.OfficeLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OfficeLocationRepository extends MongoRepository<OfficeLocation, String> {
    Optional<OfficeLocation> findFirstByOrderByIdAsc();
}

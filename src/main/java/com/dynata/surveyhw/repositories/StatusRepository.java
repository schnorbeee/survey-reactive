package com.dynata.surveyhw.repositories;

import com.dynata.surveyhw.entities.Status;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public interface StatusRepository extends ReactiveCrudRepository<Status, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO status (status_id, name)
            VALUES (:#{#s.statusId}, :#{#s.name})
            ON CONFLICT (status_id)
            DO UPDATE SET name = EXCLUDED.name
            """)
    Mono<Void> upsertStatus(Status s);
}

package io.github.codergod1337.streamplay.repository;

import io.github.codergod1337.streamplay.model.RawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RawDataRepository extends JpaRepository<RawData, Long> {

    Optional<RawData> findByValue(String value);

}

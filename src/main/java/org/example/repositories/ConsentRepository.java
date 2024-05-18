package org.example.repositories;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;
import org.example.domain.entities.Consent;

import java.util.Optional;

@Repository
public interface ConsentRepository extends PageableRepository<Consent, String> {
    Optional<Consent> findById(@NotNull String id);
}

package org.example.repositories;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;
import org.example.domain.entities.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends PageableRepository<Account, UUID> {
    Optional<Account> findById(@NotNull UUID id);
}

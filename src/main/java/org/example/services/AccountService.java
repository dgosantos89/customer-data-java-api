package org.example.services;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.example.domain.entities.Account;
import org.example.domain.entities.EnumConsentPermissions;
import org.example.domain.responses.ResponseAccount;
import org.example.domain.responses.ResponseAccountData;
import org.example.repositories.AccountRepository;
import org.example.repositories.ConsentRepository;
import org.example.util.Utils;

import java.util.Optional;
import java.util.UUID;

@Singleton
@Transactional
public class AccountService {
    @Inject
    AccountRepository accountRepository;

    @Inject
    ConsentRepository consentRepository;

    public Page<ResponseAccountData> getAccounts(Pageable pageable, String consentId) {
        Utils.validateConsent(consentId, consentRepository, EnumConsentPermissions.ACCOUNTS_READ);
        return accountRepository.findAll(Optional.ofNullable(pageable).orElse(Pageable.unpaged())).map(Account::toResponseAccountData);
    }

    public ResponseAccount getAccount(UUID accountId, String consentId) {
        Account entity = accountRepository.findById(accountId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Account Id " + accountId + " not found"));

        return entity.toResponseAccount();
    }


}

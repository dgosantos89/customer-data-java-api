package org.example.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.domain.responses.ResponseAccount;
import org.example.domain.responses.ResponseAccountData;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(name = "bank")
    private String bank;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime;

    @Column(name = "status_update_date_time")
    private LocalDateTime statusUpdateDateTime;

    public ResponseAccount toResponseAccount() {
        return new ResponseAccount().data(toResponseAccountData());
    }

    public ResponseAccountData toResponseAccountData() {
        return new ResponseAccountData()
                .id(id.toString())
                .accountNumber(accountNumber)
                .bank(bank);
    }
}

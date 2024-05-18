package org.example.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseAccountData {
    @JsonProperty("id")
    private String id;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("accountNumero")
    private String accountNumber;

    public ResponseAccountData id(String id) {
        this.id = id;
        return this;
    }

    public ResponseAccountData bank(String bank) {
        this.bank = bank;
        return this;
    }

    public ResponseAccountData accountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }
}

package com.fintech.ledger.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull
    private Integer fromWalletId;

    @NotNull
    private Integer toWalletId;

    @NotNull
    @DecimalMin(value = "0.00000001", message = "{validation.amount.positive}")
    private BigDecimal amount;

    private String description;

    private Integer tradeId;
}

package com.fintech.ledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LedgerEntryResponse {

    private Integer id;
    private Integer walletId;
    private Integer tradeId;
    private String entryType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;
}

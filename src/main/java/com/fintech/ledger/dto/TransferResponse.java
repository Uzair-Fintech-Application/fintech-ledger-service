package com.fintech.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TransferResponse {

    private String referenceId;
}

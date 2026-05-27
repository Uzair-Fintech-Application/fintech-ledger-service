package com.fintech.ledger.service;

import com.fintech.ledger.dto.TransferRequest;

public interface LedgerService {

    String transfer(TransferRequest request);
}

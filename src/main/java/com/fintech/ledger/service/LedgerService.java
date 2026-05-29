package com.fintech.ledger.service;

import com.fintech.ledger.dto.LedgerEntryResponse;
import com.fintech.ledger.dto.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LedgerService {

    String transfer(TransferRequest request);

    /**
     * Returns paginated ledger entries for a given wallet, newest first.
     */
    Page<LedgerEntryResponse> getWalletLedger(Integer walletId, Pageable pageable);

    /**
     * Returns all ledger entries for admin audit, newest first.
     */
    Page<LedgerEntryResponse> getGlobalAudit(Pageable pageable);
}


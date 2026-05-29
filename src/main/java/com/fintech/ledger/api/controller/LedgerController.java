package com.fintech.ledger.api.controller;

import com.fintech.ledger.dto.LedgerEntryResponse;
import com.fintech.ledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "Transaction history")
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping("/wallet/{walletId}")
    @Operation(summary = "Get ledger entries for a wallet")
    public ResponseEntity<Page<LedgerEntryResponse>> getWalletLedger(
            @PathVariable Integer walletId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ledgerService.getWalletLedger(walletId, pageable));
    }

    @GetMapping("/admin/audit")
    @Operation(summary = "Global Ledger Audit (Admins only)")
    public ResponseEntity<Page<LedgerEntryResponse>> getGlobalAudit(
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ledgerService.getGlobalAudit(pageable));
    }
}

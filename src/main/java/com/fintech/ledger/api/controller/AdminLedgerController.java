package com.fintech.ledger.api.controller;

import com.fintech.ledger.service.LedgerReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/ledger/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Ledger", description = "Administrative operations for the double-entry ledger")
public class AdminLedgerController {

    private final LedgerReconciliationService reconciliationService;
    private final MessageSource messageSource;

    @PostMapping("/reconcile")
    @Operation(summary = "Trigger a manual mathematical reconciliation audit of the Ledger")
    public ResponseEntity<String> triggerReconciliation() {
        reconciliationService.runManualReconciliation();
        return ResponseEntity.ok(messageSource.getMessage("success.reconciliation.triggered", null, Locale.getDefault()));
    }
}

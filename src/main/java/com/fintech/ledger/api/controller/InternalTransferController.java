package com.fintech.ledger.api.controller;

import com.fintech.ledger.dto.TransferRequest;
import com.fintech.ledger.dto.TransferResponse;
import com.fintech.ledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@Tag(name = "Internal Transfer", description = "Service-to-service fund transfer")
@Slf4j
public class InternalTransferController {

    private final LedgerService ledgerService;

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between wallets (internal)")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        log.info("INTERNAL TRANSFER REQUEST | from={} | to={} | amount={}", 
                request.getFromWalletId(), request.getToWalletId(), request.getAmount());
        String refId = ledgerService.transfer(request);
        return ResponseEntity.ok(TransferResponse.builder().referenceId(refId).build());
    }
}

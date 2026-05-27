package com.fintech.ledger.service;

import com.fintech.ledger.entity.Wallet;
import com.fintech.ledger.repository.LedgerEntryRepository;
import com.fintech.ledger.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerReconciliationService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private static final BigDecimal SYSTEM_INITIAL_BALANCE = new BigDecimal("999999999999.0000");
    private static final Integer SYSTEM_USER_ID = 1;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(readOnly = true)
    public void runDailyReconciliation() {
        log.info("RECONCILIATION_START | Initiating Ledger mathematical audit via Cron Job...");
        reconcile();
    }
    
    public void runManualReconciliation() {
        log.info("RECONCILIATION_START | Manual audit triggered via API.");
        reconcile();
    }

    private void reconcile() {
        List<Wallet> allWallets = walletRepository.findAll();
        List<Object[]> computed = ledgerEntryRepository.getComputedBalances();
        
        Map<Integer, BigDecimal> computedMap = computed.stream()
            .collect(Collectors.toMap(
                row -> (Integer) row[0],
                row -> (BigDecimal) row[1]
            ));

        int mismatches = 0;

        for (Wallet w : allWallets) {
            BigDecimal computedNet = computedMap.getOrDefault(w.getId(), BigDecimal.ZERO);
            BigDecimal expectedBalance = computedNet;
            
            if (SYSTEM_USER_ID.equals(w.getUserId())) {
                expectedBalance = expectedBalance.add(SYSTEM_INITIAL_BALANCE);
            }

            if (expectedBalance.compareTo(w.getAvailableBalance()) != 0) {
                log.error("[RECONCILIATION_FAILED] | walletId={} | userId={} | cachedBalance={} | trueComputedBalance={}", 
                    w.getId(), w.getUserId(), w.getAvailableBalance(), expectedBalance);
                mismatches++;
            }
        }

        if (mismatches == 0) {
            log.info("[RECONCILIATION_OK] | Successfully audited {} wallets. Zero data corruption found.", allWallets.size());
        } else {
            log.error("[RECONCILIATION_ERROR] | Audit complete. {} mismatches detected across {} wallets.", mismatches, allWallets.size());
        }
    }
}

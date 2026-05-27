package com.fintech.ledger.repository;

import com.fintech.ledger.entity.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Integer> {
    Page<LedgerEntry> findByWalletIdOrderByCreatedAtDesc(Integer walletId, Pageable pageable);

    @Query("SELECT l.wallet.id as walletId, SUM(CASE WHEN l.entryType = 'CREDIT' THEN l.amount ELSE -l.amount END) as netAmount " +
           "FROM LedgerEntry l GROUP BY l.wallet.id")
    List<Object[]> getComputedBalances();
}

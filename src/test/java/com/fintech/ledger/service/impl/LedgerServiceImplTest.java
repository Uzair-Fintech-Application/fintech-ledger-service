package com.fintech.ledger.service.impl;

import com.fintech.ledger.dto.TransferRequest;
import com.fintech.ledger.enums.EntryType;
import com.fintech.ledger.entity.LedgerEntry;
import com.fintech.ledger.entity.Wallet;
import com.fintech.ledger.exception.InsufficientFundsException;
import com.fintech.ledger.repository.LedgerEntryRepository;
import com.fintech.ledger.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private LedgerServiceImpl ledgerService;

    private Wallet createWallet(Integer id, BigDecimal balance, boolean system) {
        return Wallet.builder().id(id).userId(system ? 0 : 1)
                .availableBalance(balance).systemWallet(system).build();
    }

    @Test
    @DisplayName("transfer - success - creates debit and credit entries")
    void transfer_success() {
        Wallet from = createWallet(1, new BigDecimal("5000.0000"), false);
        Wallet to = createWallet(2, new BigDecimal("1000.0000"), false);

        when(walletRepository.findByIdWithLock(1)).thenReturn(Optional.of(from));
        when(walletRepository.findByIdWithLock(2)).thenReturn(Optional.of(to));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferRequest req = new TransferRequest();
        req.setFromWalletId(1);
        req.setToWalletId(2);
        req.setAmount(new BigDecimal("1000.0000"));
        req.setDescription("Test transfer");

        String refId = ledgerService.transfer(req);

        assertNotNull(refId);
        assertEquals(new BigDecimal("4000.0000"), from.getAvailableBalance());
        assertEquals(new BigDecimal("2000.0000"), to.getAvailableBalance());

        ArgumentCaptor<LedgerEntry> entryCaptor = ArgumentCaptor.forClass(LedgerEntry.class);
        verify(ledgerEntryRepository, times(2)).save(entryCaptor.capture());

        var entries = entryCaptor.getAllValues();
        assertEquals(EntryType.DEBIT, entries.get(0).getEntryType());
        assertEquals(EntryType.CREDIT, entries.get(1).getEntryType());
        assertEquals(entries.get(0).getReferenceId(), entries.get(1).getReferenceId());
    }

    @Test
    @DisplayName("transfer - insufficient funds - throws InsufficientFundsException")
    void transfer_insufficientFunds_throws() {
        Wallet from = createWallet(1, new BigDecimal("100.0000"), false);
        Wallet to = createWallet(2, new BigDecimal("0.0000"), false);

        when(walletRepository.findByIdWithLock(1)).thenReturn(Optional.of(from));
        when(walletRepository.findByIdWithLock(2)).thenReturn(Optional.of(to));

        TransferRequest req = new TransferRequest();
        req.setFromWalletId(1);
        req.setToWalletId(2);
        req.setAmount(new BigDecimal("500.0000"));

        assertThrows(InsufficientFundsException.class, () -> ledgerService.transfer(req));
        verify(walletRepository, never()).save(any());
    }

    @Test
    @DisplayName("transfer - system wallet has unlimited funds")
    void transfer_systemWallet_unlimitedFunds() {
        Wallet systemWallet = createWallet(1, new BigDecimal("-999.0000"), true);
        Wallet userWallet = createWallet(2, new BigDecimal("0.0000"), false);

        when(walletRepository.findByIdWithLock(1)).thenReturn(Optional.of(systemWallet));
        when(walletRepository.findByIdWithLock(2)).thenReturn(Optional.of(userWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferRequest req = new TransferRequest();
        req.setFromWalletId(1);
        req.setToWalletId(2);
        req.setAmount(new BigDecimal("5000.0000"));

        String refId = ledgerService.transfer(req);
        assertNotNull(refId);
        assertEquals(new BigDecimal("5000.0000"), userWallet.getAvailableBalance());
    }

    @Test
    @DisplayName("transfer - negative amount - throws IllegalArgumentException")
    void transfer_negativeAmount_throws() {
        TransferRequest req = new TransferRequest();
        req.setFromWalletId(1);
        req.setToWalletId(2);
        req.setAmount(new BigDecimal("-100"));

        assertThrows(IllegalArgumentException.class, () -> ledgerService.transfer(req));
    }
}

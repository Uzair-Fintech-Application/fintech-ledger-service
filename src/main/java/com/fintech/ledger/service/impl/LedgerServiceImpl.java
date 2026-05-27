package com.fintech.ledger.service.impl;

import com.fintech.ledger.dto.TransferRequest;
import com.fintech.ledger.enums.EntryType;
import com.fintech.ledger.entity.LedgerEntry;
import com.fintech.ledger.entity.Wallet;
import com.fintech.ledger.exception.InsufficientFundsException;
import com.fintech.ledger.exception.ResourceNotFoundException;
import com.fintech.ledger.repository.LedgerEntryRepository;
import com.fintech.ledger.repository.WalletRepository;
import com.fintech.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerServiceImpl implements LedgerService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final MessageSource messageSource;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String transfer(TransferRequest request) {
        Integer fromId = request.getFromWalletId();
        Integer toId = request.getToWalletId();
        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    messageSource.getMessage("error.transfer.amount.invalid", new Object[]{amount}, Locale.getDefault()));
        }

        log.debug("TRANSFER BEGIN | from={} | to={} | amount={} | tradeId={}", fromId, toId, amount, request.getTradeId());

        Wallet first, second;
        if (fromId < toId) {
            first = lockWallet(fromId);
            second = lockWallet(toId);
        } else {
            second = lockWallet(fromId);
            first = lockWallet(toId);
        }

        Wallet from = fromId.equals(first.getId()) ? first : second;
        Wallet to = fromId.equals(first.getId()) ? second : first;

        if (!isSystemWallet(from) && from.getAvailableBalance().compareTo(amount) < 0) {
            log.warn("TRANSFER REJECTED | reason=INSUFFICIENT_FUNDS | walletId={} | available={} | requested={}",
                    fromId, from.getAvailableBalance(), amount);
            throw new InsufficientFundsException(
                    messageSource.getMessage("error.insufficient.funds", new Object[]{fromId}, Locale.getDefault()));
        }

        String refId = UUID.randomUUID().toString();

        from.setAvailableBalance(from.getAvailableBalance().subtract(amount));
        to.setAvailableBalance(to.getAvailableBalance().add(amount));
        walletRepository.save(from);
        walletRepository.save(to);

        ledgerEntryRepository.save(LedgerEntry.builder()
                .wallet(from).tradeId(request.getTradeId())
                .entryType(EntryType.DEBIT).amount(amount)
                .balanceAfter(from.getAvailableBalance())
                .referenceId(refId).description(request.getDescription()).build());

        ledgerEntryRepository.save(LedgerEntry.builder()
                .wallet(to).tradeId(request.getTradeId())
                .entryType(EntryType.CREDIT).amount(amount)
                .balanceAfter(to.getAvailableBalance())
                .referenceId(refId).description(request.getDescription()).build());

        log.info("TRANSFER OK | ref={} | from={} | to={} | amount={} | fromBal={} | toBal={} | tradeId={}",
                refId, fromId, toId, amount, from.getAvailableBalance(), to.getAvailableBalance(), request.getTradeId());
        return refId;
    }

    private Wallet lockWallet(Integer walletId) {
        return walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.wallet.not.found", new Object[]{walletId}, Locale.getDefault())));
    }

    private boolean isSystemWallet(Wallet wallet) {
        return Boolean.TRUE.equals(wallet.getSystemWallet());
    }
}

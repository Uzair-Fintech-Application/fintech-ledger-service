package com.fintech.ledger.dto;

import com.fintech.ledger.entity.LedgerEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    @Mapping(source = "wallet.id", target = "walletId")
    LedgerEntryResponse toLedgerEntryResponse(LedgerEntry entry);
}

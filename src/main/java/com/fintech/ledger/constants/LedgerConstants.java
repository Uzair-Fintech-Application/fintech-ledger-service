package com.fintech.ledger.constants;

public final class LedgerConstants {

    private LedgerConstants() {
    }

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLE = "role";

    public static final int CURRENCY_SCALE = 8;
    public static final int RATE_SCALE = 18;
}

# Ledger Service — FinTech Platform

## Overview
The **Ledger Service** manages the double-entry accounting system. It records immutable transactions (credits and debits) between wallets and ensures systemic balance integrity. 

## Features
- Double-entry ledger logic (Transfer between wallets)
- Maintains transaction history and audit trails
- Enforces strict data consistency and atomicity for transfers
- Paginated retrieval of ledger entries per wallet and globally (admin)

## Tech Stack
- Java 21 / Spring Boot 3.4.1
- PostgreSQL (JPA/Hibernate)
- MapStruct, Lombok
- OpenAPI (SpringDoc)
- Eureka Client

## Port
`8083`

## API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/ledger/wallet/{walletId}` | Get ledger entries for a wallet |
| GET | `/api/ledger/admin/audit` | Global Ledger Audit (Admins only) |

## Configuration
Copy `.env.example` to `.env` and fill in values before running.

```bash
cp .env.example .env
mvn spring-boot:run
```

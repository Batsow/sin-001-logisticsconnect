# IngestionServiceApp

## Overview

Parses and cleans `hubs-global.csv`, a messy legacy export of hubs, sorting centers, and regional districts data, and is the
first stop in the LogisticsConnect pipeline. Independent Maven module, no parent pom.

Part of the [LogisticsConnect](../README.md) project.

## Known data issues

`hubs-global.csv` is deliberately messy — cleaning it is the point of this service. Look
out for (and handle) at least:

- **Inconsistent casing** in IDs, names, and status/category values (`Active` /
  `active` / `ACTIVE`)
- **Padding** — leading/trailing spaces, and the occasional double space, inside
  fields
- **Duplicate records** for the same real-world entity, written with a different ID
  casing/format and/or slightly different field values
- **Inconsistent date formats** (`YYYY-MM-DD`, `MM/DD/YYYY`, `DD-MM-YYYY`, one- and
  two-digit months/days) and outright invalid dates
- **Missing / placeholder values** — blank fields, `N/A`, `n/a`, `TBD`, `unknown`,
  `-`, `NaN`
- **Invalid or non-numeric values** in numeric columns (negative counts, spelled-out
  numbers, unrealistic values)
- **Inconsistent boolean/flag representations** (`Y`/`N`, `yes`/`no`, `1`/`0`,
  `true`/`FALSE`)
- **Naming/spelling variants** for the same thing (e.g. regional spelling
  differences, synonyms)

## Worked example

Two raw rows from `hubs-global.csv`:

```
hub_id, Province ,sorting_center,active
H-502 ,gauteng,Pretoria North,0
H-505,Western Cape ,Cape Town  Port,TRUE
```

A reasonable cleaned shape for those same two rows:

| hub_id | province | sorting_center | active |
|---|---|---|---|
| H-502 | Gauteng | Pretoria North | false |
| H-505 | Western Cape | Cape Town Port | true |

That covers padding (`H-502 ` → `H-502`), casing (`gauteng` → `Gauteng`), a
collapsed double space (`Cape Town  Port` → `Cape Town Port`), and boolean
normalization (`0`/`TRUE` → `false`/`true`). Column names, exact casing
convention, and boolean representation are up to you — just be consistent.

This doesn't cover deduplication: rows like `H-500`, `H-504`, `H-510`, and `H-515`
all describe "Johannesburg Central" in "Gauteng" under different hub IDs, with
conflicting `active` values between them. How you resolve that (which one wins,
how you detect they're duplicates in the first place) is part of the exercise —
there's no single correct answer, but be ready to explain your reasoning.

## Project structure

```
ingestion-service/
├── pom.xml
└── src/main/
    ├── java/co/wethinkcode/logisticsconnect/IngestionServiceApp.java
    └── resources/hubs-global.csv
```

## Build

```
mvn package
```

## Run

```
java -jar target/ingestion-service.jar
```

Listens on port `7050`. Currently just exposes `/health` — the actual CSV
parsing/cleaning logic is a TODO.

## Test

No automated tests yet. Manually verify it's up:

```
curl http://localhost:7050/health   # -> OK
```

To add real tests, add JUnit 5 + the Surefire plugin to `pom.xml`, put tests under
`src/test/java/co/wethinkcode/logisticsconnect/`, and run `mvn test`.



## Stage 1: Data Cleaning Decisions

The ingestion service reads `hubs-global.csv`, cleans the data, removes duplicate hub records, and exposes the cleaned records through the `/hubs` REST endpoint.

### Cleaning rules

The following cleaning rules are applied:

* `hub_id` values are trimmed and converted to uppercase.
* Province values are trimmed and normalized to a consistent capitalized format.
* Known province variants such as `gauteng`, `eastern cape`, and `Kwa-Zulu Natal` are normalized.
* Sorting-center names are trimmed, internal extra spaces are removed, and the names are normalized to a consistent format.
* Boolean values such as `Y`, `yes`, `1`, and `true` are converted to `true`.
* Boolean values such as `N`, `no`, `0`, and `false` are converted to `false`.
* Missing or placeholder values such as blank values, `N/A`, `TBD`, `unknown`, `-`, and `NaN` are represented as `null`.

The current CSV does not contain date or numeric fields, so date and numeric normalization is not required for the current dataset.

### Duplicate strategy

A duplicate hub is identified using the cleaned `province` and `sorting_center` values.

For example, records that both represent:

```text
Gauteng | Johannesburg Central
```

are treated as the same real-world hub, even when their `hub_id` or other formatting differs.

When duplicates are found, the ingestion service keeps the **first occurrence** and discards later occurrences.

This strategy was chosen because it is simple, deterministic, and easy to understand. The service does not attempt to automatically merge conflicting values such as different `active` statuses. The first record is retained, which avoids silently inventing a value that was not present in the source data.

The original CSV contains 18 records. After cleaning and duplicate removal, the ingestion service exposes 12 unique hub records.

### REST endpoint

The cleaned hub records are exposed through:

```text
GET /hubs
```

The endpoint returns the cleaned hub data as JSON so that other services can consume the ingestion service rather than reading the raw CSV directly.


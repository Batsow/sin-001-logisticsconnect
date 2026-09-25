# LogisticsConnect

LogisticsConnect is a Java-based systems integration project that demonstrates data cleaning, REST APIs, asynchronous messaging, service integration, and automated alerting.

The project is split into independent Maven services that communicate through REST and ActiveMQ.

## Architecture

```text
                         +----------------------+
                         |      hubs-global.csv |
                         +----------+-----------+
                                    |
                                    v
                         +----------------------+
                         |   Ingestion Service  |
                         |       :7050          |
                         +----------+-----------+
                                    |
                                    | REST
                                    v
                         +----------------------+
                         |     Hub Service      |
                         |       :7051          |
                         +----------+-----------+
                                    |
                                    |
             +----------------------+----------------------+
             |                                             |
             | REST                                        |
             v                                             v
+--------------------------+                 +--------------------------+
|    Delay Stage Service   |                 |     Transit Service      |
|          :7052           |                 |          :7053           |
+------------+-------------+                 +------------+-------------+
             |                                            ^
             |                                            |
             | ActiveMQ                                   |
             | package-status-topic                       |
             v                                            |
        +-------------------------------------------------+
        |                   ActiveMQ                       |
        +---------------------------+---------------------+
                                    |
                                    |
                                    v
                         +----------------------+
                         |       AlertBot       |
                         |       :7054          |
                         +----------------------+
```

## Services

| Service             | Port | Responsibility                                                                     |
| ------------------- | ---: | ---------------------------------------------------------------------------------- |
| Ingestion Service   | 7050 | Reads and cleans hub data and exposes the cleaned records through REST             |
| Hub Service         | 7051 | Provides hub information to other services                                         |
| Delay Stage Service | 7052 | Stores delay stages and publishes stage changes to ActiveMQ                        |
| Transit Service     | 7053 | Calculates ETA information using hub data and asynchronously received delay stages |
| AlertBot            | 7054 | Consumes delay-stage messages and produces simulated alerts                        |

## Track 1: Foundations of Java Messaging

The project uses Java Message Service (JMS) with ActiveMQ Classic for asynchronous communication.

The ActiveMQ broker is configured in:

```text
common/docker-compose.yml
```

The shared message configuration contains:

```text
Topic: package-status-topic
```

### Message flow

```text
Delay Stage Service
        |
        | publishes delay-stage change
        v
ActiveMQ topic
package-status-topic
        |
        +----------------------+
        |                      |
        v                      v
Transit Service           AlertBot
```

When a delay stage changes, the Delay Stage Service publishes a JSON message:

```json
{
  "hubId": "H-500",
  "stage": 7
}
```

Transit Service and AlertBot subscribe to this topic independently.

Transit Service stores the received stage locally and uses it when calculating ETA.

AlertBot evaluates the stage against its alert threshold and produces a simulated alert when the threshold is reached.

## Track 2: Advanced REST and JSON Serialization

REST endpoints are used for synchronous communication between services.

### Ingestion Service

```text
GET /hubs
```

Returns the cleaned hub records.

Example:

```json
[
  {
    "hubId": "H-500",
    "province": "Gauteng",
    "sortingCenter": "Johannesburg Central",
    "active": true
  }
]
```

### Hub Service

```text
GET /hubs/{hubId}
```

Returns information for a specific hub.

Example:

```json
{
  "hubId": "H-500",
  "province": "Gauteng",
  "sortingCenter": "Johannesburg Central",
  "active": true
}
```

An unknown hub returns:

```text
404 Not Found
```

### Delay Stage Service

```text
GET /delay-stage/{hubId}
```

Returns the current delay stage.

Example:

```json
{
  "hubId": "H-500",
  "stage": 7
}
```

The delay stage can be changed with:

```text
PUT /delay-stage/{hubId}?stage=7
```

Valid stages are:

```text
0 to 8
```

Invalid or missing stages return:

```text
400 Bad Request
```

### Transit Service

```text
GET /eta/{hubId}
```

Returns an ETA-shaped JSON response.

Example:

```json
{
  "hubId": "H-500",
  "sortingCenter": "Johannesburg Central",
  "delayStage": 7,
  "etaMinutes": 100
}
```

Unknown hubs return:

```text
404 Not Found
```

## Stage 1: Data Cleaning

The source file contains inconsistent casing, spacing, province names, IDs, boolean values, missing values, and duplicate hub records.

Example raw records:

```text
H-500, Gauteng ,Johannesburg Central,Y
h-501,Western Cape,Cape Town Port,yes
H-502 ,gauteng,Pretoria North,0
H-506,Kwa-Zulu Natal,Durban Harbour,1
```

The cleaning process performs the following:

### Hub IDs

IDs are trimmed and converted to uppercase.

```text
h-501
```

becomes:

```text
H-501
```

### Provinces

Province values are trimmed and normalized to consistent casing.

Known variants of KwaZulu-Natal are normalized to:

```text
KwaZulu-Natal
```

### Sorting centres

Padding and repeated spaces are removed and names are normalized.

### Active values

The following values are treated as `true`:

```text
yes
y
1
true
```

The following values are treated as `false`:

```text
no
n
0
false
```

Unknown values become `null` instead of being left as raw placeholders.

### Missing values and placeholders

Values such as:

```text
unknown
n/a
tbd
-
nan
```

are treated as missing and represented as `null`.

### Duplicate handling

After normalization, duplicate records representing the same real-world hub are detected using the normalized province and sorting centre.

The first occurrence is retained and later duplicates are discarded.

This prevents casing or spelling inconsistencies from creating multiple records for the same hub.

The cleaned data contains:

```text
11 unique hub records
```

## Stage 3: Asynchronous Messaging

The Delay Stage Service publishes only when a hub's stage actually changes.

For exam


WTC-L7CP2L56

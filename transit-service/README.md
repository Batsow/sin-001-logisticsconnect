# Transit Service

Transit Service calculates estimated arrival information for hubs using cleaned hub data from Hub Service and delay-stage information received asynchronously through ActiveMQ.

## Port

```text
7053
```

## Responsibilities

Transit Service:

* retrieves hub information from Hub Service
* receives delay-stage updates asynchronously through ActiveMQ
* stores the latest known delay stage for each hub
* calculates an ETA using the current delay stage
* exposes the ETA through a REST endpoint

## Architecture

```text
                         +----------------------+
                         |     Hub Service      |
                         |       :7051          |
                         +----------+-----------+
                                    |
                                    | REST
                                    v
                              Transit Service
                                  :7053
                                    ^
                                    |
                                    | stored stage
                                    |
+----------------------+            |
| Delay Stage Service  |            |
|        :7052         |            |
+----------+-----------+            |
           |                        |
           | JMS publish             |
           v                        |
      +-------------------------------+
      |          ActiveMQ             |
      |   package-status-topic        |
      +-------------------------------+
```

Transit Service does not need to synchronously query Delay Stage Service whenever an ETA is requested.

Instead, it subscribes to delay-stage events and maintains the latest received value locally.

## REST Endpoint

### Get ETA

```text
GET /eta/{hubId}
```

Example:

```text
GET /eta/H-500
```

Response:

```json
{
  "hubId": "H-500",
  "sortingCenter": "Johannesburg Central",
  "delayStage": 7,
  "etaMinutes": 100
}
```

The response contains:

```text
hubId
sortingCenter
delayStage
etaMinutes
```

### Unknown Hub

When the requested hub does not exist, Transit Service returns:

```text
404 Not Found
```

## Health Endpoint

Transit Service exposes:

```text
GET /health
```

Expected response:

```text
OK
```

## ActiveMQ Integration

Transit Service subscribes to:

```text
package-status-topic
```

Delay Stage Service publishes messages when a hub's delay stage changes.

Example message:

```json
{
  "hubId": "H-500",
  "stage": 7
}
```

Transit Service receives this message and updates its local delay-stage store.

## Message Processing Flow

```text
Delay Stage Service
        |
        | {hubId, stage}
        v
ActiveMQ topic
        |
        v
TransitDelayStageSubscriber
        |
        v
TransitDelayStageStore
        |
        v
GET /eta/{hubId}
        |
        v
ETA response
```

## Main Components

### TransitDelayStageStore

`TransitDelayStageStore` keeps the latest delay stage for each hub.

It provides:

```java
updateStage(String hubId, int stage)
```

to update a hub's stage.

It provides:

```java
getStage(String hubId)
```

to retrieve a hub's current stage.

When no stage has been received for a hub, the store defaults to:

```text
0
```

### TransitDelayStageSubscriber

`TransitDelayStageSubscriber` creates the JMS connection to ActiveMQ and subscribes to:

```text
package-status-topic
```

When a text message arrives, it:

1. reads the message body
2. deserializes the JSON
3. extracts the hub ID
4. extracts the stage
5. updates `TransitDelayStageStore`

### TransitServiceApp

`TransitServiceApp` exposes the ETA endpoint and uses the stored delay-stage value when calculating the ETA.

The production application starts the subscriber before starting the Javalin server.

## ETA Calculation

The current ETA formula is:

```text
etaMinutes = 30 + (delayStage × 10)
```

Examples:

| Delay Stage |         ETA |
| ----------: | ----------: |
|           0 |  30 minutes |
|           1 |  40 minutes |
|           2 |  50 minutes |
|           3 |  60 minutes |
|           4 |  70 minutes |
|           5 |  80 minutes |
|           6 |  90 minutes |
|           7 | 100 minutes |
|           8 | 110 minutes |

This is a deliberately simple demonstration formula.

The main purpose is to show that an asynchronous delay-stage event changes the result returned by the REST endpoint.

## Example End-to-End Update

Suppose the current stage for `H-500` is:

```text
0
```

The ETA is:

```text
30 minutes
```

The Delay Stage Service then receives:

```text
PUT /delay-stage/H-500?stage=7
```

It publishes:

```json
{
  "hubId": "H-500",
  "stage": 7
}
```

Transit Service receives the event and updates its local store.

A later request:

```text
GET /eta/H-500
```

returns:

```json
{
  "hubId": "H-500",
  "sortingCenter": "Johannesburg Central",
  "delayStage": 7,
  "etaMinutes": 100
}
```

This demonstrates the asynchronous integration between the services.

## Build

From the `transit-service` directory:

```powershell
mvn package
```

The packaged application is:

```text
target/transit-service.jar
```

## Run

```powershell
java -jar target/transit-service.jar
```

The service listens on:

```text
7053
```

At startup it also connects to ActiveMQ and begins subscribing to:

```text
package-status-topic
```

## Testing

Run:

```powershell
mvn test
```

The current Transit Service test suite contains:

```text
13 tests
0 failures
0 errors
```

The tests cover:

* ETA endpoint behavior
* Hub Service integration
* unknown hubs
* delay-stage handling
* ETA calculation
* delay-stage storage
* JSON message handling
* ActiveMQ subscriber behavior
* real ActiveMQ message delivery
* MQ-driven ETA calculation

## Test-Driven Development

Transit Service functionality was developed using TDD.

The tests cover both isolated behavior and integration behavior.

Unit-level tests verify the local store and message handling.

REST tests verify:

* JSON responses
* status codes
* ETA calculations
* interaction with Hub Service

Messaging tests verify that a real ActiveMQ delay-stage event can reach Transit Service and affect the returned ETA.

## Design Decision: Local Delay-Stage Store

Transit Service stores the latest received delay stage locally rather than making a synchronous request to Delay Stage Service for every ETA request.

This demonstrates an asynchronous messaging pattern:

```text
event -> subscriber -> local state -> REST response
```

### Advantage

The ETA endpoint can use the latest locally received stage without making another service-to-service call for the delay value.

### Tradeoff

The local state is in memory and is lost when Transit Service restarts.

It also represents the latest event received by the service rather than persistent historical state.

## Design Decision: Hub Service Integration

Transit Service still retrieves hub information from Hub Service because the ETA response needs hub-specific information such as the sorting centre.

This gives the system two different integration styles:

```text
Hub data:
REST / synchronous

Delay-stage updates:
ActiveMQ / asynchronous
```

This makes the contrast between REST and messaging explicit.

## Project Structure

```text
transit-service/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── co/
    │           └── wethinkcode/
    │               └── logisticsconnect/
    │                   ├── TransitServiceApp.java
    │                   ├── TransitDelayStageStore.java
    │                   └── TransitDelayStageSubscriber.java
    └── test/
        └── java/
            └── co/
                └── wethinkcode/
                    └── logisticsconnect/
```

## Java Version

Transit Service is configured to compile against:

```text
Java 17
```

using Maven's:

```xml
<maven.compiler.release>17</maven.compiler.release>
```

## Conclusion

Transit Service demonstrates how a REST API can combine synchronous service integration with asynchronous messaging.

Hub information is retrieved from Hub Service through REST, while delay-stage changes are delivered through ActiveMQ and stored locally.

The resulting ETA endpoint demonstrates that an asynchronous event can directly affect the data returned by a REST API.

# AlertBot

AlertBot is the alerting component of the LogisticsConnect system.

It subscribes to delay-stage events published through ActiveMQ and produces a simulated alert when a hub reaches the configured delay threshold.

## Port

```text
7054
```

## Responsibilities

AlertBot:

* subscribes to the `package-status-topic` ActiveMQ topic
* receives JSON delay-stage messages
* extracts the hub ID and delay stage
* evaluates the delay stage against an alert threshold
* produces a simulated alert when the threshold is reached

The current alert threshold is:

```text
stage >= 5
```

## Architecture

```text
Delay Stage Service
        |
        | publishes {hubId, stage}
        v
ActiveMQ
package-status-topic
        |
        +----------------------+
        |                      |
        v                      v
Transit Service           AlertBot
                              |
                              v
                     AlertEvaluator
                              |
                     stage >= 5 ?
                              |
                         yes  |
                              v
                   SimulatedAlertNotifier
                              |
                              v
                    console alert message
```

## ActiveMQ

AlertBot uses Java Messaging Service (JMS) with ActiveMQ Classic.

The broker configuration is shared through:

```text
common/
```

and the topic is:

```text
package-status-topic
```

AlertBot creates a JMS consumer for this topic when the application starts.

## Message Format

AlertBot expects a JSON message with a hub ID and delay stage:

```json
{
  "hubId": "H-500",
  "stage": 7
}
```

The message is published by Delay Stage Service.

## Main Components

### AlertEvaluator

`AlertEvaluator` contains the alert rule.

It receives the configured threshold when it is created:

```java
new AlertEvaluator(5)
```

The alert condition is:

```text
stage >= threshold
```

For the current implementation:

```text
stage >= 5
```

causes an alert.

### AlertBotMessageHandler

`AlertBotMessageHandler` is responsible for processing the JSON message.

It:

1. deserializes the JSON message
2. extracts `hubId`
3. extracts `stage`
4. asks `AlertEvaluator` whether an alert is required
5. calls `AlertNotifier` when the threshold is reached

### AlertNotifier

`AlertNotifier` is an interface used to separate the alert decision from the notification mechanism.

This makes the alerting logic easy to test and allows another notification implementation to be added later.

### SimulatedAlertNotifier

The current notifier is:

```text
SimulatedAlertNotifier
```

Instead of calling an external platform, it prints the alert to the console.

Example:

```text
ALERT: Hub H-500 has reached delay stage 7
```

### AlertBotMessageSubscriber

`AlertBotMessageSubscriber` manages the JMS connection.

It:

* creates the ActiveMQ connection
* creates a JMS session
* creates the topic consumer
* registers a message listener
* passes received text messages to `AlertBotMessageHandler`

## Application Startup

`AlertBotApp` creates the application components:

```text
AlertEvaluator
       |
       v
AlertBotMessageHandler
       |
       v
AlertBotMessageSubscriber
       |
       v
ActiveMQ
```

The subscriber starts before the Javalin application is started.

AlertBot also exposes a health endpoint.

## REST Endpoint

AlertBot is primarily an event-driven consumer, but it exposes:

```text
GET /health
```

on port:

```text
7054
```

Expected response:

```text
OK
```

## Build

From the `alertbot` directory:

```powershell
mvn package
```

The packaged application is:

```text
target/alertbot.jar
```

## Run

```powershell
java -jar target/alertbot.jar
```

The application listens on:

```text
7054
```

## Testing

Run:

```powershell
mvn test
```

The current AlertBot test suite contains:

```text
9 tests
0 failures
0 errors
```

The tests cover:

* alert threshold evaluation
* message handling
* notification behavior
* subscriber behavior
* real ActiveMQ message delivery

## Test-Driven Development

The AlertBot functionality was developed using TDD.

Tests cover the behavior of the main components before relying on the full application flow.

The design separates the decision logic from the notification logic, which allows the core behavior to be tested without requiring an external notification platform.

## End-to-End Flow

The complete AlertBot event flow is:

```text
1. Delay Stage Service changes a hub's delay stage.
2. Delay Stage Service publishes the stage change to ActiveMQ.
3. ActiveMQ delivers the message to AlertBot.
4. AlertBotMessageSubscriber receives the message.
5. AlertBotMessageHandler deserializes the JSON.
6. AlertEvaluator checks the stage.
7. When stage >= 5, SimulatedAlertNotifier sends the alert.
```

Example:

```text
PUT /delay-stage/H-500?stage=7
```

publishes:

```json
{
  "hubId": "H-500",
  "stage": 7
}
```

AlertBot then produces:

```text
ALERT: Hub H-500 has reached delay stage 7
```

## Example Alert Thresholds

With the current threshold of `5`:

| Stage | Alert |
| ----: | ----- |
|     0 | No    |
|     1 | No    |
|     2 | No    |
|     3 | No    |
|     4 | No    |
|     5 | Yes   |
|     6 | Yes   |
|     7 | Yes   |
|     8 | Yes   |

## Design Tradeoff

The notification mechanism is simulated rather than connected to a real social media, SMS, email, or webhook provider.

This provides a deterministic demonstration without external credentials or third-party availability requirements.

The `AlertNotifier` interface keeps the implementation open for a real notification provider to be added later.

## Project Structure

```text
alertbot/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── co/
    │           └── wethinkcode/
    │               └── logisticsconnect/
    │                   ├── AlertBotApp.java
    │                   ├── AlertBotMessageHandler.java
    │                   ├── AlertBotMessageSubscriber.java
    │                   ├── AlertEvaluator.java
    │                   ├── AlertNotifier.java
    │                   └── SimulatedAlertNotifier.java
    └── test/
        └── java/
            └── co/
                └── wethinkcode/
                    └── logisticsconnect/
```

## Java Version

AlertBot is configured to compile against:

```text
Java 17
```

using Maven's:

```xml
<maven.compiler.release>17</maven.compiler.release>
```

## Conclusion

AlertBot demonstrates the event-driven part of LogisticsConnect.

It consumes the same delay-stage event used by Transit Service, evaluates the event against a business rule, and produces a simulated alert without tightly coupling the alerting logic to an external notification provider.

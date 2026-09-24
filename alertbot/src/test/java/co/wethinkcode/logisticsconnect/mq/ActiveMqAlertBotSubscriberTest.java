package co.wethinkcode.logisticsconnect.mq;

import co.wethinkcode.logisticsconnect.AlertBotMessageHandler;
import co.wethinkcode.logisticsconnect.AlertBotMessageSubscriber;
import co.wethinkcode.logisticsconnect.AlertEvaluator;
import co.wethinkcode.logisticsconnect.AlertNotifier;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActiveMqAlertBotSubscriberTest {

    @Test
    void shouldReceiveMessageFromActiveMqAndRaiseAlert() throws Exception {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        List<String> alerts = new ArrayList<>();

        AlertNotifier notifier = (hubId, stage) ->
                alerts.add(hubId + ":" + stage);

        AlertBotMessageHandler handler =
                new AlertBotMessageHandler(evaluator, notifier);

        AlertBotMessageSubscriber subscriber =
                new AlertBotMessageSubscriber(handler);

        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        Connection connection = factory.createConnection();

        Session session = connection.createSession(
                false,
                Session.AUTO_ACKNOWLEDGE
        );

        Topic topic = session.createTopic(MqConfig.TOPIC);

        subscriber.start();

        try {
            MessageProducer producer =
                    session.createProducer(topic);

            connection.start();

            TextMessage message = session.createTextMessage(
                    "{\"hubId\":\"H-500\",\"stage\":7}"
            );

            producer.send(message);

            for (int i = 0; i < 20; i++) {
                if (alerts.contains("H-500:7")) {
                    break;
                }

                Thread.sleep(100);
            }

            assertEquals(
                    List.of("H-500:7"),
                    alerts
            );

            producer.close();
        } finally {
            subscriber.stop();
            session.close();
            connection.close();
        }
    }
}
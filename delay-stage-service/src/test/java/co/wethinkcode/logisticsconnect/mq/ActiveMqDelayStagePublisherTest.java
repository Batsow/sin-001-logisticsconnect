package co.wethinkcode.logisticsconnect.mq;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class ActiveMqDelayStagePublisherTest {
    @Test
    void shouldPublishDelayStageMessageToTopic() throws Exception {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        Connection connection = factory.createConnection();
        Session session = connection.createSession(
                false,
                Session.AUTO_ACKNOWLEDGE
        );

        Topic topic = session.createTopic(MqConfig.TOPIC);
        MessageConsumer consumer = session.createConsumer(topic);

        connection.start();

        ActiveMqDelayStagePublisher publisher =
                new ActiveMqDelayStagePublisher();

        publisher.publish("H-TEST", 4);

        Message message = consumer.receive(3000);

        assertNotNull(message);

        String body = ((javax.jms.TextMessage) message).getText();
        assertTrue(body.contains("\"hubId\":\"H-TEST\""));
        assertTrue(body.contains("\"stage\":4"));

        consumer.close();
        session.close();
        connection.close();
    }
}

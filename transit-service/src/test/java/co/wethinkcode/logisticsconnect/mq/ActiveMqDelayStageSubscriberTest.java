package co.wethinkcode.logisticsconnect.mq;

import co.wethinkcode.logisticsconnect.TransitDelayStageStore;
import co.wethinkcode.logisticsconnect.TransitDelayStageSubscriber;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActiveMqDelayStageSubscriberTest {

    @Test
    void shouldReceiveDelayStageMessageAndUpdateStore() throws Exception {
        TransitDelayStageStore store = new TransitDelayStageStore();

        TransitDelayStageSubscriber messageHandler =
                new TransitDelayStageSubscriber(store);

        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        Connection connection = factory.createConnection();
        Session session = connection.createSession(
                false,
                Session.AUTO_ACKNOWLEDGE
        );

        Topic topic = session.createTopic(MqConfig.TOPIC);

        messageHandler.start();

        MessageProducer producer = session.createProducer(topic);

        connection.start();

        TextMessage message = session.createTextMessage(
                "{\"hubId\":\"H-500\",\"stage\":6}"
        );

        producer.send(message);

        Thread.sleep(500);

        assertEquals(6, store.getStage("H-500"));

        producer.close();
        session.close();
        connection.close();

        messageHandler.stop();
    }
}
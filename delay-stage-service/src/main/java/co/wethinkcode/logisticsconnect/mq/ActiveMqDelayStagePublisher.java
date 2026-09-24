package co.wethinkcode.logisticsconnect.mq;

import co.wethinkcode.logisticsconnect.DelayStagePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.util.Map;

public class ActiveMqDelayStagePublisher implements DelayStagePublisher {

    private final ActiveMQConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;

    public ActiveMqDelayStagePublisher() {
        connectionFactory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
        objectMapper = new ObjectMapper();
    }

    @Override
    public void publish(String hubId, int stage) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            connection = connectionFactory.createConnection();

            session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE
            );

            Topic topic = session.createTopic(MqConfig.TOPIC);

            producer = session.createProducer(topic);

            Map<String, Object> messageData = Map.of(
                    "hubId", hubId,
                    "stage", stage
            );

            String json = objectMapper.writeValueAsString(messageData);

            TextMessage message = session.createTextMessage(json);

            connection.start();

            producer.send(message);

        } catch (JsonProcessingException | javax.jms.JMSException e) {
            throw new RuntimeException("Could not publish delay stage message", e);

        } finally {
            try {
                if (producer != null) {
                    producer.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (session != null) {
                    session.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ignored) {
            }
        }
    }
}
package co.wethinkcode.logisticsconnect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.wethinkcode.logisticsconnect.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import java.util.Map;

public class TransitDelayStageSubscriber {

    private final TransitDelayStageStore store;
    private final ObjectMapper objectMapper;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public TransitDelayStageSubscriber(TransitDelayStageStore store) {
        this.store = store;
        this.objectMapper = new ObjectMapper();
    }

    public void handleMessage(String messageBody) throws Exception {
        Map<String, Object> message = objectMapper.readValue(
                messageBody,
                new TypeReference<Map<String, Object>>() {}
        );

        String hubId = (String) message.get("hubId");
        int stage = ((Number) message.get("stage")).intValue();

        store.updateStage(hubId, stage);
    }

    public void start() throws Exception {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        connection = factory.createConnection();

        session = connection.createSession(
                false,
                Session.AUTO_ACKNOWLEDGE
        );

        Topic topic = session.createTopic(MqConfig.TOPIC);

        consumer = session.createConsumer(topic);

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage) {
                    String body = ((TextMessage) message).getText();
                    handleMessage(body);
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "Could not process delay stage message",
                        e
                );
            }
        });

        connection.start();
    }

    public void stop() throws Exception {
        if (consumer != null) {
            consumer.close();
        }

        if (session != null) {
            session.close();
        }

        if (connection != null) {
            connection.close();
        }
    }
}
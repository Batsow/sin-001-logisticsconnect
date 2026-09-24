package co.wethinkcode.logisticsconnect;

import co.wethinkcode.logisticsconnect.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class AlertBotMessageSubscriber {

    private final AlertBotMessageHandler handler;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public AlertBotMessageSubscriber(AlertBotMessageHandler handler) {
        this.handler = handler;
    }

    public void handleMessage(String messageBody) throws Exception {
        handler.handleMessage(messageBody);
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
                        "Could not process alert message",
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
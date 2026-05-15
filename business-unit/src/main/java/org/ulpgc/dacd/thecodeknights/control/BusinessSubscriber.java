package org.ulpgc.dacd.thecodeknights.control;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class BusinessSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(BusinessSubscriber.class);

    private final String brokerUrl;
    private final String topicName;
    private final String clientId;
    private final String subscriptionName;
    private final EventRouter router;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public BusinessSubscriber(String brokerUrl, String topicName, String clientId,
                               String subscriptionName, EventRouter router) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.clientId = clientId;
        this.subscriptionName = subscriptionName;
        this.router = router;
    }

    public void start() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        connection = factory.createConnection();
        connection.setClientID(clientId);
        connection.setExceptionListener(e ->
                logger.error("Conexión JMS perdida en Business Unit ({}): {}", topicName, e.getMessage()));

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        consumer = session.createDurableSubscriber(topic, subscriptionName);

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage textMessage) {
                    router.route(textMessage.getText());
                }
            } catch (JMSException e) {
                logger.error("Error procesando mensaje JMS: {}", e.getMessage());
            }
        });

        connection.start();
        logger.info("Suscriptor Business Unit iniciado. Topic: {}", topicName);
    }

    public void stop() {
        try {
            if (consumer != null) consumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            logger.error("Error cerrando Business Unit subscriber: {}", e.getMessage());
        }
    }
}

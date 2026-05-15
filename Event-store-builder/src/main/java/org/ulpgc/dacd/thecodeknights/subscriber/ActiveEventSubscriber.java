package org.ulpgc.dacd.thecodeknights.subscriber;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.eventcontroller.EventMessageController;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class ActiveEventSubscriber implements EventSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(ActiveEventSubscriber.class);

    private final String brokerUrl;
    private final String topicName;
    private final String clientId;
    private final String subscriptionName;
    private final EventMessageController controller;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public ActiveEventSubscriber(
            String brokerUrl,
            String topicName,
            String clientId,
            String subscriptionName,
            EventMessageController controller
    ) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.clientId = clientId;
        this.subscriptionName = subscriptionName;
        this.controller = controller;
    }

    @Override
    public void start() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        connection = factory.createConnection();

        connection.setClientID(clientId);
        connection.setExceptionListener(exception -> {
            logger.error("Conexión JMS perdida en Event Store Builder: {}", exception.getMessage());
        });

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic(topicName);

        consumer = session.createDurableSubscriber(topic, subscriptionName);

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage textMessage) {
                    controller.handle(topicName, textMessage.getText());
                } else {
                    logger.warn("Mensaje ignorado: no es TextMessage");
                }
            } catch (JMSException e) {
                logger.error("Error procesando mensaje JMS: {}", e.getMessage());
            }
        });

        connection.start();

        logger.info("Suscriptor durable iniciado.");
        logger.info("Broker: {}", brokerUrl);
        logger.info("Topic: {}", topicName);
        logger.info("Client ID: {}", clientId);
        logger.info("Subscription: {}", subscriptionName);
    }

    @Override
    public void stop() {
        try {
            if (consumer != null) consumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            logger.error("Error cerrando subscriber: {}", e.getMessage());
        }
    }
}

package org.ulpgc.dacd.thecodeknights.event;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.lang.IllegalStateException;

public class SteamEventPublisher {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "steam.games";
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public void start() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = factory.createConnection();
        connection.setExceptionListener(exception -> {
            System.err.println("Conexión JMS perdida en SteamEventPublisher: " + exception.getMessage());
            stop();
        });
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic(TOPIC_NAME);
        producer = session.createProducer(topic);

        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void publish(String json) throws JMSException {
        if (session == null || producer == null || connection == null) {
            throw new IllegalStateException("SteamEventPublisher no iniciado");
        }
        try{
        TextMessage message = session.createTextMessage(json);
        producer.send(message);
        }
        catch (JMSException e){
            System.err.println("Error publicando evento. Se intentará reconectar: " + e.getMessage());
            reconnect();
            TextMessage retryMessage = session.createTextMessage(json);
            producer.send(retryMessage);
        }

    }
    private void reconnect() throws JMSException {
        stop();
        start();
    }

    public void stop() {
        try {
            if (producer != null) producer.close();
        } catch (JMSException e) {
            System.err.println("Error cerrando producer: " + e.getMessage());
        }

        try {
            if (session != null) session.close();
        } catch (JMSException e) {
            System.err.println("Error cerrando session: " + e.getMessage());
        }

        try {
            if (connection != null) connection.close();
        } catch (JMSException e) {
            System.err.println("Error cerrando connection: " + e.getMessage());
        }

        producer = null;
        session = null;
        connection = null;
    }
}

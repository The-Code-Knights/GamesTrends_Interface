package org.ulpgc.dacd.thecodeknights.control.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import javax.jms.*;
import java.lang.IllegalStateException;
import java.util.List;

public class TwitchEventPublisher {

    private final String brokerUrl;
    private final String topicName;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public TwitchEventPublisher(String brokerUrl, String topicName) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
    }

    public void start() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        connection = factory.createConnection();
        connection.setExceptionListener(e -> stop());
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);

        producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void publish(List<TwitchEvent> events) throws JMSException {
        if (session == null || producer == null || connection == null) {
            throw new IllegalStateException("TwitchEventPublisher no iniciado");
        }

        for (TwitchEvent event : events) {
            String json = gson.toJson(event);
            try {
                TextMessage message = session.createTextMessage(json);
                producer.send(message);
            } catch (JMSException e) {
                reconnect();
                TextMessage retryMessage = session.createTextMessage(json);
                producer.send(retryMessage);
            }
        }
    }

    private void reconnect() throws JMSException {
        stop();
        start();
    }

    public void stop() {
        try { if (producer != null) producer.close(); } catch (Exception ignored) {}
        try { if (session != null) session.close(); } catch (Exception ignored) {}
        try { if (connection != null) connection.close(); } catch (Exception ignored) {}

        producer = null;
        session = null;
        connection = null;
    }
}

package org.ulpgc.dacd.thecodeknights.controller.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.thecodeknights.model.SteamEvent;

import javax.jms.*;

import java.lang.IllegalStateException;
import java.util.List;

public class SteamEventPublisher {

    private final String brokerUrl;
    private final String topicName;
    private final Gson gson;

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public SteamEventPublisher(String brokerUrl, String topicName) {
        validateConfiguration(brokerUrl, topicName);

        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }

    public void start() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        connection = factory.createConnection();

        connection.setExceptionListener(exception -> {
            System.err.println("Conexión JMS perdida en SteamEventPublisher: " + exception.getMessage());
            stop();
        });

        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic(topicName);
        producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void publish(List<SteamEvent> events) {
        for (SteamEvent event : events) {
            if (!isPublishable(event)) continue;

            try {
                String json = toJson(event);
                send(json);

                System.out.println("Evento Steam publicado: " + json);

            } catch (Exception e) {
                System.err.println("Error publicando evento Steam: " + e.getMessage());
            }
        }
    }

    private String toJson(SteamEvent event) {
        return gson.toJson(event);
    }

    private void send(String json) throws JMSException {
        ensureStarted();

        try {
            TextMessage message = session.createTextMessage(json);
            producer.send(message);

        } catch (JMSException e) {
            System.err.println("Error publicando evento. Se intentará reconectar: " + e.getMessage());

            reconnect();

            TextMessage retryMessage = session.createTextMessage(json);
            producer.send(retryMessage);
        }
    }

    private boolean isPublishable(SteamEvent event) {
        return event != null
                && event.getTs() != null
                && !event.getTs().isBlank()
                && event.getSs() != null
                && !event.getSs().isBlank()
                && event.getAppId() != null
                && !event.getAppId().isBlank()
                && event.getPlayers() != null;
    }

    private void ensureStarted() {
        if (connection == null || session == null || producer == null) {
            throw new IllegalStateException("SteamEventPublisher no iniciado");
        }
    }

    private void reconnect() throws JMSException {
        stop();
        start();
    }

    public void stop() {
        closeErrors();

        producer = null;
        session = null;
        connection = null;
    }


    private void closeErrors(){
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            System.err.println("Error cerrando producer: " + e.getMessage());
        }
    }

    private void validateConfiguration(String brokerUrl, String topicName) {
        if (brokerUrl == null || brokerUrl.isBlank()) {
            throw new IllegalArgumentException("brokerUrl cannot be null or blank");
        }

        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("topicName cannot be null or blank");
        }
    }
}


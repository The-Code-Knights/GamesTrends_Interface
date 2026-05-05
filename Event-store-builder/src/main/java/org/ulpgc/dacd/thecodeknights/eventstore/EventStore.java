package org.ulpgc.dacd.thecodeknights.eventstore;

public interface EventStore {

    void save(String topicName, String jsonEvent);
}

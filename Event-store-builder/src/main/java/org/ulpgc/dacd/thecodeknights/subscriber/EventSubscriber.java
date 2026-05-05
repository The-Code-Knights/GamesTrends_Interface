package org.ulpgc.dacd.thecodeknights.subscriber;

public interface EventSubscriber {
    void start() throws Exception;
    void stop();
}


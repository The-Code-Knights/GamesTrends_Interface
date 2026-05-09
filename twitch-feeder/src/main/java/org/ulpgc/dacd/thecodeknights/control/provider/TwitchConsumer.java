package org.ulpgc.dacd.thecodeknights.control.provider;

import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import java.io.IOException;
import java.util.List;

public interface TwitchConsumer {

    List<TwitchEvent> fetchEvents(int totalEvents) throws IOException;

    int getApiCallCount();
}

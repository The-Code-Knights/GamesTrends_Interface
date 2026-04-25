package org.ulpgc.dacd.thecodeknights.control;

import org.ulpgc.dacd.thecodeknights.model.Stream;

import java.io.IOException;
import java.util.List;

public interface TwitchConsumer {

    List<Stream> fetchStreams(int totalStreams) throws IOException;

    int getApiCallCount();
}
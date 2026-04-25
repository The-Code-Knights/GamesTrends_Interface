package org.ulpgc.dacd.thecodeknights.control;

import org.ulpgc.dacd.thecodeknights.model.Stream;

import java.util.List;

public interface TwitchStore {
    void save(List<Stream> streams);
}
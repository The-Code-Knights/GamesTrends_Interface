package org.ulpgc.dacd.thecodeknights.controller.provider;

import org.ulpgc.dacd.thecodeknights.model.SteamEvent;

import java.util.List;

public interface SteamConsumer {

    List<SteamEvent> getEvents();
}

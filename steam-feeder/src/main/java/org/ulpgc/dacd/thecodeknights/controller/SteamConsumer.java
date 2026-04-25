package org.ulpgc.dacd.thecodeknights.controller;

import org.ulpgc.dacd.thecodeknights.model.SteamGame;

import java.util.List;

public interface SteamConsumer {
    List<SteamGame> getGames();
}

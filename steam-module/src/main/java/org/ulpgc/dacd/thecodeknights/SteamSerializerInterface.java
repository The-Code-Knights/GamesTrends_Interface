package org.ulpgc.dacd.thecodeknights;

import java.util.List;

public interface SteamSerializerInterface {
    void save(List<SteamGame> games);

    void printAllGames(); //Para testear, luego borrar
}

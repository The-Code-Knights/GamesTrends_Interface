package fakes;

import org.ulpgc.dacd.thecodeknights.model.Stream;
import org.ulpgc.dacd.thecodeknights.control.TwitchStore;

import java.util.ArrayList;
import java.util.List;

public class FakeTwitchStore implements TwitchStore {

    public List<Stream> saved = new ArrayList<>();

    @Override
    public void save(List<Stream> streams) {
        saved.addAll(streams);
    }
}
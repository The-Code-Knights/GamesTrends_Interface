package fakes;

import org.ulpgc.dacd.thecodeknights.event.TwitchEventPipeline;
import org.ulpgc.dacd.thecodeknights.model.Stream;

import java.util.ArrayList;
import java.util.List;

public class FakeTwitchEventPipeline extends TwitchEventPipeline {

    public List<Stream> received = new ArrayList<>();

    public FakeTwitchEventPipeline() {
        super(null, null);
    }

    @Override
    public void process(List<Stream> streams) {
        received.addAll(streams);
    }
}

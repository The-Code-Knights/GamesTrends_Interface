import fakes.FakeTwitchConsumer;
import fakes.FakeTwitchEventPipeline;
import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.control.TwitchController;

import static org.junit.jupiter.api.Assertions.*;

class TwitchControllerTest {

    @Test
    void execute_shouldFetchAndSendEvents() {

        FakeTwitchConsumer consumer = new FakeTwitchConsumer();
        FakeTwitchEventPipeline pipeline = new FakeTwitchEventPipeline();

        TwitchController controller = new TwitchController(consumer, pipeline);

        controller.execute();

        assertEquals(2, pipeline.received.size());
        assertEquals("user1", pipeline.received.get(0).getUserName());
        assertEquals("user2", pipeline.received.get(1).getUserName());
    }
}

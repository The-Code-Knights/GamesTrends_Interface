import fakes.FakeTwitchConsumer;
import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.control.TwitchController;
import org.ulpgc.dacd.thecodeknights.control.store.TwitchEventPublisher;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwitchControllerTest {

    @Test
    void execute_fetchesAndPublishesEvents() {
        FakeTwitchConsumer consumer = new FakeTwitchConsumer();
        List<TwitchEvent> received = new ArrayList<>();

        TwitchEventPublisher fakePublisher = new TwitchEventPublisher(null, null) {
            @Override
            public void publish(List<TwitchEvent> events) throws JMSException {
                received.addAll(events);
            }
        };

        TwitchController controller = new TwitchController(consumer, fakePublisher);
        controller.execute();

        assertEquals(2, received.size());
        assertEquals("user1", received.get(0).getUserName());
        assertEquals("user2", received.get(1).getUserName());
    }
}

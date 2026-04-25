import fakes.FakeTwitchConsumer;
import fakes.FakeTwitchStore;
import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.control.TwitchController;


import static org.junit.jupiter.api.Assertions.*;

class TwitchControllerTest {

    @Test
    void execute_shouldFetchAndStoreStreams() {

        FakeTwitchConsumer consumer = new FakeTwitchConsumer();
        FakeTwitchStore store = new FakeTwitchStore();

        TwitchController controller = new TwitchController(consumer, store);

        controller.execute();

        assertEquals(2, store.saved.size());
        assertEquals("user1", store.saved.get(0).getUserName());
        assertEquals("user2", store.saved.get(1).getUserName());
    }
}
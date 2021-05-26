package interview;

import interview.model.Client;
import interview.model.ClientDataStore;
import interview.model.ClientRequest;
import interview.model.ClientResponse;
import interview.model.SearchBy;
import interview.service.SearchEngineService;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestApp {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    private static ClientDataStore dataStore;

    private final SearchEngineService searchEngineService = new SearchEngineService();

    private CountDownLatch latch;


    @BeforeAll
    static void init() {

        // init client data store
        dataStore = ClientDataStore.getInstance();
        dataStore.populateClientDataStore();

        // config log4j
        BasicConfigurator.configure();
    }


    @Test
    void testMultiThreadRequestDuringOneMinute() throws InterruptedException {

        // set request object
        final ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setSearchBy(SearchBy.FIRST);

        // set countdown latch
        latch = new CountDownLatch(60);


        final Runnable periodicRequestTask = () -> {
            final ClientResponse response = searchEngineService.processRequest(request);

            if (response != null) {
                logger.info("Printing thread results: " + response.toString());

                // assertions
                if (latch.getCount() <= 27) {
                    assertFalse(containsClientWithFirstName(response.getData(), "John5"));
                }
                if (latch.getCount() <= 18) {
                    assertTrue(containsClientWithFirstName(response.getData(), "John6"));
                }
            }
            latch.countDown();
        };


        final AtomicInteger count = new AtomicInteger(5);
        final Runnable periodicAddClientTask = () -> {
            final int i = count.getAndIncrement();
            final Client newClient = new Client("John" + i, "Smith" + i);
            logger.info(">>> Adding new client" + newClient.toString());
            dataStore.addClient(newClient);
        };

        final ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(4);
        executor.scheduleAtFixedRate(periodicRequestTask, 0, 10, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(periodicRequestTask, 0, 10, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(periodicRequestTask, 0, 10, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(periodicAddClientTask, 0, 20, TimeUnit.SECONDS);

        latch.await(20, TimeUnit.SECONDS);
        assertTrue(containsClientWithFirstName(dataStore.getClients(), "John5"));

        latch.await(40, TimeUnit.SECONDS);
        assertTrue(containsClientWithFirstName(dataStore.getClients(), "John6"));
        assertTrue(containsClientWithFirstName(dataStore.getClients(), "John7"));

        // shutdown
        executor.shutdown();

    }


    /**
     * Helper method to check if a list of clients contains a client with specific first name.
     *
     * @param firstName - first name to search
     * @return true if found
     */
    private boolean containsClientWithFirstName(final List<Client> data, final String firstName) {
        return data.stream().anyMatch(it-> Objects.equals(it.getFirstName(), firstName));
    }

}

package interview;

import interview.model.Client;
import interview.model.ClientDataStore;
import interview.model.ClientRequest;
import interview.model.ClientResponse;
import interview.model.SearchBy;
import interview.service.SearchEngineService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class App {

    /**
     * Main method for problem definition:
     * 3 threads running in parallel which send a client request every 10sec
     * 1 additional thread adds a new client every 20sec
     * latest clients added should be included in client response
     *
     * @param args
     */
    public static void main (String[] args) {

        final ClientDataStore dataStore = ClientDataStore.getInstance();
        dataStore.populateClientDataStore();

        final ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setSearchBy(SearchBy.FIRST);

        final SearchEngineService engine = new SearchEngineService();

        final Runnable periodicRequestTask = () -> {
            final ClientResponse response = engine.processRequest(request);
            if (response != null) {
                System.out.println("Printing thread " + Thread.currentThread().getName() + " results: " + response.toString());
            }
        };

        final AtomicInteger count = new AtomicInteger(5);
        final Runnable periodicAddClientTask = () -> {
            final int i = count.getAndIncrement();
            final Client newClient = new Client("John" + i, "Smith" + i);
            System.out.println(">> Thread " + Thread.currentThread().getName() + " adding new client" + newClient.toString());
            dataStore.addClient(newClient);
        };

        final ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(4);
        executor.scheduleAtFixedRate(periodicRequestTask, 0, 10, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(periodicRequestTask, 0, 10, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(periodicRequestTask, 0, 10, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(periodicAddClientTask, 0, 20, TimeUnit.SECONDS);

    }


}

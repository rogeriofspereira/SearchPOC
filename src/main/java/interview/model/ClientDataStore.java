package interview.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Client data store class
 */
public class ClientDataStore {

    private static ClientDataStore instance;

    private final List<Client> clients = Collections.synchronizedList(new ArrayList<>());

    // private constructor
    private ClientDataStore() {}

    /**
     * Synchronized method to retrieve ClientDataStore instance.
     * For newer versions of JVM synchronized all method is now a preferred solution to double-checked locking
     * for lazy initialization. Another option is to use an inner static class to hold the reference instead.
     *
     * @return {@link ClientDataStore} instance
     */
    public static synchronized ClientDataStore getInstance() {
        if(instance == null) {
            instance = new ClientDataStore();
        }
        return instance;
    }

    /**
     * Populates the client data store
     */
    public void populateClientDataStore() {

        for (int i = 0; i < 5; i++) {
            Client client = new Client("John" + i, "Smith" + i);
            clients.add(client);
        }

        for (int i = 0; i < 5; i++) {
            Client client = new Client("Adam" + i, "Thomas" + i);
            clients.add(client);
        }

        for (int i = 0; i < 5; i++) {
            Client client = new Client("Anne" + i, "Simpson" + i);
            clients.add(client);
        }

    }

    public List<Client> getClients() {
        return clients;
    }

    public void addClient(Client client) {
        clients.add(client);
    }

}

package interview.service;

import interview.model.Client;
import interview.model.ClientDataStore;
import interview.model.ClientRequest;
import interview.model.ClientResponse;
import interview.model.SearchBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The search engine service.
 */
public class SearchEngineService {

    private final ClientDataStore dataStore = ClientDataStore.getInstance();

    /**
     * Method to process client request
     *
     * @param request - client request object
     * @return ClientResponse
     */
    public ClientResponse processRequest(final ClientRequest request) {

        final List<Client> dataResponse = Collections.synchronizedList(new ArrayList<>());
        final ClientResponse response = new ClientResponse(dataResponse);

        synchronized(dataStore.getClients()) {

            for (Client client : dataStore.getClients()) {

                if (request.getSearchBy().equals(SearchBy.FIRST)) {
                    if (client.getFirstName().startsWith(request.getFirstName())) {
                        dataResponse.add(client);
                    }

                } else {
                    if (client.getLastName().startsWith(request.getLastName())) {
                        dataResponse.add(client);
                    }
                }

            }

        }

        return response;
    }

}

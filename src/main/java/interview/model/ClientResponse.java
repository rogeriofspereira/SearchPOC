package interview.model;

import java.util.List;

/**
 * Client response class
 */
public class ClientResponse {

    private List<Client> data;

    public ClientResponse(List<Client> data) {
        this.data = data;
    }

    public List<Client> getData() {
        return data;
    }

    public void setData(List<Client> data) {
        this.data = data;
    }

    @Override
    public synchronized String toString() {
        return "ClientResponse{" +
                "data=" + data +
                '}';
    }
}

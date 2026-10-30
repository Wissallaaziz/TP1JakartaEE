package ma.emsi.Laazizwissal.service;
import java.io.Serializable;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Dependent
public class LlmClientPourGemini implements Serializable {

    private final String key;

    private Client clientRest;

    private final WebTarget target;

    public LlmClientPourGemini() {

        this.key = System.getenv("GEMINI_KEY");

        if (this.key == null || this.key.isBlank()) {
            throw new IllegalStateException(
                    "La variable d'environnement GEMINI_KEY est absente."
            );
        }

        this.clientRest = ClientBuilder.newClient();

        this.target = clientRest.target(
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + "gemini-3.5-flash-lite:generateContent"
        ).queryParam("key", this.key);
    }

    public Response envoyerRequete(Entity requestEntity) {
        Invocation.Builder request =
                target.request(MediaType.APPLICATION_JSON_TYPE);

        return request.post(requestEntity);
    }

    public void closeClient() {
        this.clientRest.close();
    }
}

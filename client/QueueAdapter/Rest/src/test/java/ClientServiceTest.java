import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;

import Model.dto.ClientDto;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

public class ClientServiceTest extends AbstractContainerBase {

    @Test
    public void getAllClientsTest() {
        Response response = client
                .target(BASE_URL)
                .path("client/clients")
                .request(MediaType.APPLICATION_JSON)
                .get();


        Collection<ClientDto> clientDTOList = response
                .readEntity(new GenericType<Collection<ClientDto>>() {
                });

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(24, clientDTOList.size());
    }

    @Test
    public void getClientTest() {
        Response response = client
                .target(BASE_URL)
                .path("client/1")
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(400, response.getStatus());

        response = client
                .target(BASE_URL)
                .path("client/clients")
                .request(MediaType.APPLICATION_JSON)
                .get();
        Collection<ClientDto> clientDTOList = response
                .readEntity(new GenericType<Collection<ClientDto>>() {
                });
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(24, clientDTOList.size());

        ClientDto clientDTO = clientDTOList.toArray(new ClientDto[0])[0];
        response = client
                .target(BASE_URL)
                .path("client/" + clientDTO.getId())
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertEquals(200, response.getStatus());
        ClientDto getClient = response.readEntity(ClientDto.class);
        Assertions.assertEquals(clientDTO, getClient);
    }

    @Test
    public void updateClientTest() {
        Response response = client
                .target(BASE_URL)
                .path("client/clients")
                .request(MediaType.APPLICATION_JSON)
                .get();
        Collection<ClientDto> clientDTOList = response
                .readEntity(new GenericType<Collection<ClientDto>>() {
                });

        ClientDto clientDTO = clientDTOList.toArray(new ClientDto[0])[0];

        response = client
                .target(BASE_URL)
                .path("client/update")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(clientDTO), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, response.getStatus());

        response = client
                .target(BASE_URL)
                .path("client/" + clientDTO.getId())
                .request(MediaType.APPLICATION_JSON)
                .get();
        ClientDto getClient = response.readEntity(ClientDto.class);
        Assertions.assertEquals(clientDTO, getClient);
    }
}
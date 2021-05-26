package dev.danielpadua.micronautexampleapi;

import dev.danielpadua.micronautexampleapi.contracts.CustomerContract;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import io.micronaut.http.client.annotation.*;
import org.junit.jupiter.api.function.Executable;

import javax.inject.Inject;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Property(name = "SAMPLEAPI_API_KEY", value = TestConstants.APIKEY)
public class CustomerControllerTest {

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    public void testAuthN() {
        String securedUrl = "/customers";

        // when: 'Acessing a secured URL without authenticating'
        Executable eWithoutAuth = () -> client.toBlocking()
                .exchange(HttpRequest.GET(securedUrl).accept(MediaType.APPLICATION_JSON));

        // then: 'returns unauthorized'
        HttpClientResponseException exWithoutThrown = assertThrows(HttpClientResponseException.class, eWithoutAuth);
        assertEquals(exWithoutThrown.getStatus(), HttpStatus.UNAUTHORIZED);


        // when: 'Acessing a secured URL with wrong authentication'
        Executable eWrongAuth = () -> client.toBlocking()
                .exchange(HttpRequest.GET(securedUrl)
                .header("api-key", UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON));

        // then: 'returns unauthorized'
        HttpClientResponseException eWrongThrown = assertThrows(HttpClientResponseException.class, eWrongAuth);
        assertEquals(exWithoutThrown.getStatus(), HttpStatus.UNAUTHORIZED);


        // when: 'A secured URL is accessed with correct api-key auth'
        HttpResponse<String> rsp = client.toBlocking().exchange(HttpRequest.GET(securedUrl)
                        .header("api-key", TestConstants.APIKEY)
                        .accept(MediaType.APPLICATION_JSON));

        // then: 'the endpoint can be accessed
        assertEquals(rsp.getStatus(), HttpStatus.OK);
    }

    @Test
    public void testGetAll() {
        String getAllUrl = "/customers";

        // when: 'A getAll URL is accessed with correct api-key auth'
        HttpResponse<String> rsp = client.toBlocking().exchange(HttpRequest.GET(getAllUrl)
                .header("api-key", TestConstants.APIKEY)
                .accept(MediaType.APPLICATION_JSON),
                String.class);

        // then: 'the endpoint returns an OK not empty response'
        assertEquals(rsp.getStatus(), HttpStatus.OK);
        assertNotNull(rsp.getBody().get());
    }
}

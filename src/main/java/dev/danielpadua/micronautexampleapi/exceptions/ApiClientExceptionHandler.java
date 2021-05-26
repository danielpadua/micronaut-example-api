package dev.danielpadua.micronautexampleapi.exceptions;

import dev.danielpadua.micronautexampleapi.Constants;
import dev.danielpadua.micronautexampleapi.contracts.ErrorContract;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;

import javax.inject.Singleton;
import java.time.ZonedDateTime;

@Produces
@Singleton
@Requires(classes = { ApiClientException.class, ExceptionHandler.class })
public class ApiClientExceptionHandler implements ExceptionHandler<ApiClientException, HttpResponse<?>> {

    @Override
    public HttpResponse<ErrorContract> handle(HttpRequest request, ApiClientException exception) {
        return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorContract(
                        ZonedDateTime.now(Constants.ZONE_ID),
                        exception.getErrorCode(),
                        exception.getMessage()
                ));
    }
}

package dev.danielpadua.micronautexampleapi.exceptions;

import dev.danielpadua.micronautexampleapi.Constants;
import dev.danielpadua.micronautexampleapi.contracts.ErrorContract;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.StringJoiner;

@Produces
@Replaces(ConstraintExceptionHandler.class)
@Requires(classes = { ConstraintExceptionHandler.class, ExceptionHandler.class })
public class ApiClientValidationExceptionHandler extends ConstraintExceptionHandler {
    @Override
    public HttpResponse handle(HttpRequest request, ConstraintViolationException exception) {
        StringJoiner sj = new StringJoiner("; ");
        for (ConstraintViolation v : exception.getConstraintViolations()) {
            sj.add(buildMessage(v));
        }

        return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorContract(
                        ZonedDateTime.now(Constants.ZONE_ID),
                        Constants.ApiReturnCodes.INVALID_REQUEST_PAYLOAD_CODE,
                        sj.toString().trim()
                ));
    }

    @Override
    protected String buildMessage(ConstraintViolation violation) {
        Path propertyPath = violation.getPropertyPath();
        StringBuilder message = new StringBuilder();
        Iterator i = propertyPath.iterator();

        while (i.hasNext()) {
            Path.Node node = (Path.Node) i.next();
            if (node.getKind() != ElementKind.METHOD && node.getKind() != ElementKind.CONSTRUCTOR) {
                message.append(node.getName());
                if (i.hasNext()) {
                    message.append('.');
                }
            }
        }

        message.append(": ").append(violation.getMessage());
        return message.toString();
    }
}

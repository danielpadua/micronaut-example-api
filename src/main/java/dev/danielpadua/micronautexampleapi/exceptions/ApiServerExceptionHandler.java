package dev.danielpadua.micronautexampleapi.exceptions;

import dev.danielpadua.micronautexampleapi.Constants;
import dev.danielpadua.micronautexampleapi.contracts.ErrorContract;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static net.logstash.logback.argument.StructuredArguments.v;

@Produces
@Singleton
@Requires(classes = { Exception.class, ExceptionHandler.class })
public class ApiServerExceptionHandler implements ExceptionHandler<Exception, HttpResponse<?>> {
    private static final Logger LOG = LoggerFactory.getLogger(ApiServerExceptionHandler.class);

    @Override
    public HttpResponse<?> handle(HttpRequest request, Exception exception) {
        // extract stack trace from exception
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        LOG.error("ERROR_LOG",
                v("exception", new HashMap<String, String>() {{
                    put("message", exception.getMessage());
                    put("stack_trace", sw.toString());
                }}));
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorContract(
                        ZonedDateTime.now(Constants.ZONE_ID),
                        Constants.ApiReturnCodes.UNKNOWN_ERROR_CODE,
                        MessageFormat.format("There was an error while processing request. Verify requisition_id={0}",
                                MDC.get("requisition_id"))
                ));
    }
}

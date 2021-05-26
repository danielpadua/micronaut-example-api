package dev.danielpadua.micronautexampleapi.infra.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danielpadua.micronautexampleapi.ApplicationConfig;
import dev.danielpadua.micronautexampleapi.Constants;
import dev.danielpadua.micronautexampleapi.contracts.ErrorContract;
import dev.danielpadua.micronautexampleapi.utils.StopWatch;
import io.micronaut.core.annotation.Order;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static dev.danielpadua.micronautexampleapi.Constants.ApiReturnCodes.UNAUTHORIZED_CODE;
import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.argument.StructuredArguments.v;

@Filter(patterns = {"/customers/**"})
@Order(0)
public class AuthNFilter implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthNFilter.class);

    private final ObjectMapper mapper;
    private final ApplicationConfig appConfig;

    public AuthNFilter(final ObjectMapper mapper, final ApplicationConfig appConfig) {
        this.mapper = mapper;
        this.appConfig = appConfig;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        StopWatch sw = new StopWatch();
        sw.start();
        String apiKey = request.getHeaders().get("api-key") == null ? "" :
                request.getHeaders().get("api-key");

        if (!apiKey.equals(appConfig.getApiKey())) {
            sw.stop();
            MDC.put("apikey_validation_timespent", String.valueOf(sw.getElapsedTime().toMillis()));
            MutableHttpResponse response = HttpResponseFactory.INSTANCE.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorContract(
                            ZonedDateTime.now(Constants.ZONE_ID),
                            UNAUTHORIZED_CODE,
                            "Invalid or absent api-key header")
                    );
            logUnauthorized(request, response, sw.getElapsedTime().toMillis());
            return Flowable.just(response);
        }

        sw.stop();
        MDC.put("apikey_validation_timespent", String.valueOf(sw.getElapsedTime().toMillis()));

        return chain.proceed(request);
    }

    private void logUnauthorized(HttpRequest<?> request, MutableHttpResponse<?> response, long apikeyValidationTimespent) {
        Object reqObject = request.getBody().isPresent() ? request.getBody().get() : null;
        Object resObject = response.getBody().isPresent() ? response.getBody().get() : null;
        JsonNode resFormattedJson = null;
        String hostAddress = "";
        String hostName = "";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
            hostName = InetAddress.getLocalHost().getHostName();
            resFormattedJson = mapper.readTree(mapper.writeValueAsString(resObject));
        } catch (JsonProcessingException | UnknownHostException e) {
            e.printStackTrace();
        }

        String finalHostName = hostName;
        String finalHostAddress = hostAddress;

        LOG.info("UNAUTHORIZED_LOG",
                kv("http_path", request.getPath()),
                kv("http_method", request.getMethod()),
                kv("query_string", request.getUri().getQuery()),
                kv("http_status", 401),
                kv("server", new HashMap<String, String>() {{
                    put("host_address", finalHostAddress);
                    put("host_name", finalHostName);
                    put("remote_addr", request.getRemoteAddress().getAddress().getHostAddress());
                    put("remote_host", request.getRemoteAddress().getHostString());
                    put("server_name", request.getServerName());
                }}),
                kv("api_timespent", 0),
                kv("apikey_validation_timespent", apikeyValidationTimespent),
                kv("total_timespent", apikeyValidationTimespent),
                v("request_headers", request.getHeaders().asMap()),
                v("request_payload", reqObject),
                v("response_headers", response.getHeaders().asMap()),
                v("response_payload", resFormattedJson)
        );
    }
}

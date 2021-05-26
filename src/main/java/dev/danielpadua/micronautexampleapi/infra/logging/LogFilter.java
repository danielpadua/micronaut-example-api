package dev.danielpadua.micronautexampleapi.infra.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danielpadua.micronautexampleapi.utils.StopWatch;
import io.micronaut.core.annotation.Order;
import io.micronaut.http.HttpRequest;
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
import java.util.HashMap;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.argument.StructuredArguments.v;

@Filter(patterns = {"/customers/**"})
@Order(1)
public class LogFilter implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);
    private final ObjectMapper mapper;

    public LogFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String requisitionId = UUID.randomUUID().toString();
        MDC.put("requisition_id", requisitionId);
        StopWatch sw = new StopWatch();
        sw.start();

        return Flowable.fromPublisher(chain.proceed(request))
                .doAfterNext(res -> {
                    sw.stop();
                    Object reqObject = request.getBody().isPresent() ? request.getBody() : null;
                    Object resObject = res.getBody().isPresent() ? res.getBody() : null;
                    JsonNode resFormattedJson = mapper.readTree(mapper.writeValueAsString(resObject));
                    long apiTimespent = sw.getElapsedTime().toMillis();
                    long apikeyValidationTimespent = Long.parseLong(MDC.get("apikey_validation_timespent"));
                    long totalTimespent = apiTimespent + apikeyValidationTimespent;

                    LOG.info("ANALYTIC_LOG",
                            kv("http_path", request.getPath()),
                            kv("http_method", request.getMethod()),
                            kv("query_string", request.getUri().getQuery()),
                            kv("http_status", res.getStatus().getCode()),
                            kv("server", new HashMap<String, String>() {{
                                put("host_address", InetAddress.getLocalHost().getHostAddress());
                                put("host_name", InetAddress.getLocalHost().getHostName());
                                put("remote_addr", request.getRemoteAddress().getAddress().getHostAddress());
                                put("remote_host", request.getRemoteAddress().getHostString());
                                put("server_name", request.getServerName());
                            }}),
                            kv("api_timespent", apiTimespent),
                            kv("apikey_validation_timespent", apikeyValidationTimespent),
                            kv("total_timespent", totalTimespent),
                            v("request_headers", request.getHeaders().asMap()),
                            v("request_payload", reqObject),
                            v("response_headers", res.getHeaders().asMap()),
                            v("response_payload", resFormattedJson)
                    );
                });
    }
}

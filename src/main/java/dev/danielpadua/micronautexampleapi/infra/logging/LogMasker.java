package dev.danielpadua.micronautexampleapi.infra.logging;

import com.fasterxml.jackson.core.JsonStreamContext;
import dev.danielpadua.micronautexampleapi.Constants;
import net.logstash.logback.mask.ValueMasker;

public class LogMasker implements ValueMasker {

    @Override
    public Object mask(JsonStreamContext jsonStreamContext, Object o) {
        if (Constants.MASKED_STRING.contains(jsonStreamContext.getCurrentName())) {
            return "****";
        }
        return o;
    }
}

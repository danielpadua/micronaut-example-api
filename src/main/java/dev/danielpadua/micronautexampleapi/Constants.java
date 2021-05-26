package dev.danielpadua.micronautexampleapi;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final ZoneId ZONE_ID = ZoneId.of("UTC");
    public static final List<String> MASKED_STRING = Arrays.asList("email"); // put json properties to mask here

    private Constants() { }

    public static class ApiReturnCodes {
        public static final String UNAUTHORIZED_CODE = "401";
        public static final String UNKNOWN_ERROR_CODE = "500";
        public static final String INVALID_REQUEST_PAYLOAD_CODE = "422-1";

        private ApiReturnCodes() { }
    }
}

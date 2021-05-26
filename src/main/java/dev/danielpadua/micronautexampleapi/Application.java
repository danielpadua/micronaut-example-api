package dev.danielpadua.micronautexampleapi;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
        type = SecuritySchemeType.APIKEY,
        name = "apiKey",
        in = SecuritySchemeIn.HEADER,
        paramName = "api-key"
)
@OpenAPIDefinition(
        info = @Info(
                title = "micronaut-example-api",
                version = "0.1",
                description = "Micronaut Example API made by danielpadua",
                contact = @Contact(url = "https://danielpadua.dev", name = "Daniel Padua - Website",
                            email = "daniel.padua@outlook.com")
        ),
        security = @SecurityRequirement(name = "apiKey")
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }


}

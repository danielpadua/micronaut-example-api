package dev.danielpadua.micronautexampleapi.controllers;

import dev.danielpadua.micronautexampleapi.ApplicationConfig;
import dev.danielpadua.micronautexampleapi.contracts.CustomerContract;
import dev.danielpadua.micronautexampleapi.contracts.ErrorContract;
import dev.danielpadua.micronautexampleapi.models.CustomerModel;
import dev.danielpadua.micronautexampleapi.services.CustomerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller("/customers")
@Validated
public class CustomerController {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;
    private final ModelMapper mapper;
    private final ApplicationConfig config;

    public CustomerController(@Named("CustomerServiceImpl") CustomerService customerService, ModelMapper mapper,
                              ApplicationConfig config) {
        this.customerService = customerService;
        this.mapper = mapper;
        this.config = config;
    }

    @Operation(summary = "Returns all customers")

    @ApiResponse(responseCode = "200", description = "Customers fetched successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CustomerContract.class))))

    @ApiResponse(responseCode = "404", description = "No customers registered",
            content = @Content(mediaType = "application/json"))

    @ApiResponse(responseCode = "401", description = "Invalid or absent API-KEY header",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "500", description = "Unknown error while processing request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @Get(uri = "/", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<List<CustomerContract>>> getAll() {
        List<CustomerContract> customers = customerService.getAll().stream()
                .map(c -> mapper.map(c, CustomerContract.class))
                .collect(Collectors.toList());

        if (customers.isEmpty()) {
            return Single.just(HttpResponse.notFound());
        }

        return Single.just(HttpResponse.ok(customers));
    }

    @Operation(summary = "Returns customer by uuid")

    @ApiResponse(responseCode = "200", description = "Customer fetched successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomerContract.class)))

    @ApiResponse(responseCode = "404", description = "Specified customer not found",
            content = @Content(mediaType = "application/json"))

    @ApiResponse(responseCode = "401", description = "Invalid or absent API-KEY header",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "500", description = "Unknown error while processing request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @Get(uri = "/{uuid}", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<CustomerContract>> getByUuid(@PathVariable String uuid) {
        Optional<CustomerModel> customerModel = customerService.getByUuid(uuid);

        if (!customerModel.isPresent()) {
            return Single.just(HttpResponse.notFound());
        }

        CustomerContract customerContract = mapper.map(customerModel.get(), CustomerContract.class);

        return Single.just(HttpResponse.ok(customerContract));
    }

    @Operation(summary = "Creates a customer")

    @ApiResponse(responseCode = "201", description = "Customer created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomerContract.class)))

    @ApiResponse(responseCode = "422", description = "Invalid client request payload",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "401", description = "Invalid or absent API-KEY header",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "500", description = "Unknown error while processing request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @Post(uri = "/", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<CustomerContract>> create(@Valid @Body CustomerContract customerContract) throws URISyntaxException {
        CustomerModel customerModelToCreate = mapper.map(customerContract, CustomerModel.class);

        CustomerModel customerModelCreated = customerService.insert(customerModelToCreate);

        CustomerContract customerContractCreated = mapper.map(customerModelCreated, CustomerContract.class);

        return Single.just(HttpResponse.created(customerContractCreated, new URI(
                MessageFormat.format("{0}/customers/{1}", config.getBaseUri(), customerContractCreated.getUuid())
        )));
    }

    @Operation(summary = "Updates a customer")

    @ApiResponse(responseCode = "204", description = "Customer updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerContract.class)))

    @ApiResponse(responseCode = "404", description = "Specified customer not found",
            content = @Content(mediaType = "application/json"))

    @ApiResponse(responseCode = "422", description = "Invalid client request payload",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "401", description = "Invalid or absent API-KEY header",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "500", description = "Unknown error while processing request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @Put(uri = "/{uuid}", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<CustomerContract>> update(@PathVariable String uuid,
                                                         @Valid @Body CustomerContract customerContract) {
        CustomerModel customerModelToUpdate = mapper.map(customerContract, CustomerModel.class);
        customerModelToUpdate.setUuid(uuid);
        boolean updated = customerService.update(customerModelToUpdate);

        if (!updated) {
            return Single.just(HttpResponse.notFound());
        }

        return Single.just(HttpResponse.noContent());
    }

    @Operation(summary = "Deletes a customer")

    @ApiResponse(responseCode = "204", description = "Customer deleted successfully",
            content = @Content(mediaType = "application/json"))

    @ApiResponse(responseCode = "422", description = "Invalid client request payload",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "401", description = "Invalid or absent API-KEY header",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @ApiResponse(responseCode = "500", description = "Unknown error while processing request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContract.class)))

    @Delete(uri = "/{uuid}", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<CustomerContract>> delete(@PathVariable String uuid) {
        boolean deleted = customerService.deleteByUuid(uuid);

        if (!deleted) {
            return Single.just(HttpResponse.notFound());
        }

        return Single.just(HttpResponse.noContent());
    }
}
package dev.danielpadua.micronautexampleapi.models;

import java.time.LocalDate;

public class CustomerModelBuilder {
    private String id;
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthdate;

    public CustomerModelBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public CustomerModelBuilder setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public CustomerModelBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public CustomerModelBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public CustomerModelBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public CustomerModelBuilder setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public CustomerModel createCustomerModel() {
        return new CustomerModel(id, uuid, firstName, lastName, email, birthdate);
    }
}
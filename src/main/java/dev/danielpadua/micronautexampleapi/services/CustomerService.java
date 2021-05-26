package dev.danielpadua.micronautexampleapi.services;

import dev.danielpadua.micronautexampleapi.models.CustomerModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface CustomerService {
    List<CustomerModel> getAll();
    Optional<CustomerModel> getByUuid(String uuid);
    CustomerModel insert(CustomerModel customer);
    boolean update(CustomerModel customer);
    boolean deleteByUuid(String uuid);
}

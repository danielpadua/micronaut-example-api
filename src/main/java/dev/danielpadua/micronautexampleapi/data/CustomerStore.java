package dev.danielpadua.micronautexampleapi.data;

import dev.danielpadua.micronautexampleapi.models.CustomerModel;

import java.util.List;
import java.util.function.Predicate;

public interface CustomerStore {
    List<CustomerModel> getAll();
    List<CustomerModel> filter(Predicate<CustomerModel> query);
    List<CustomerModel> insert(CustomerModel... customers);
    long update(CustomerModel... customers);
    long delete(Predicate<CustomerModel> query);
}

package dev.danielpadua.micronautexampleapi.services;

import dev.danielpadua.micronautexampleapi.data.CustomerStore;
import dev.danielpadua.micronautexampleapi.models.CustomerModel;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class CustomerServiceImpl implements CustomerService {

    private final CustomerStore customerStore;

    public CustomerServiceImpl(@Named("CustomerStoreImpl") CustomerStore customerStore) {
        this.customerStore = customerStore;
    }

    @Override
    public List<CustomerModel> getAll() {
        return customerStore.getAll();
    }

    @Override
    public Optional<CustomerModel> getByUuid(String uuid) {
        return customerStore.filter(c -> c.getUuid().equals(uuid)).stream().findFirst();
    }

    @Override
    public CustomerModel insert(CustomerModel customer) {
         return customerStore.insert(customer).stream().findFirst().get();
    }

    @Override
    public boolean update(CustomerModel customer) {
        long updated = customerStore.update(customer);
        return updated == 1 ? true : false;
    }

    @Override
    public boolean deleteByUuid(String uuid) {
        long deleted = customerStore.delete(c -> c.getUuid().equals(uuid));
        return deleted == 1 ? true : false;
    }
}

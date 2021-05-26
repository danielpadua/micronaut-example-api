package dev.danielpadua.micronautexampleapi.data;

import com.github.javafaker.Faker;
import dev.danielpadua.micronautexampleapi.Constants;
import dev.danielpadua.micronautexampleapi.models.CustomerModel;
import dev.danielpadua.micronautexampleapi.models.CustomerModelBuilder;

import javax.inject.Singleton;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton
public class CustomerStoreImpl implements CustomerStore {
    private final List<CustomerModel> customersStore = new ArrayList<>();

    public CustomerStoreImpl() {
        Faker faker = new Faker();
        for (int i = 0; i < faker.random().nextInt(20, 1000); i++) {
            customersStore.add(new CustomerModelBuilder()
                    .setId(faker.internet().uuid())
                    .setUuid(faker.internet().uuid())
                    .setFirstName(faker.name().firstName())
                    .setLastName(faker.name().lastName())
                    .setEmail(faker.internet().emailAddress())
                    .setBirthdate(faker.date().birthday().toInstant().atZone(Constants.ZONE_ID).toLocalDate())
                    .createCustomerModel()
            );
        }
    }

    @Override
    public List<CustomerModel> getAll() {
        return customersStore;
    }

//    @Override
//    public Optional<CustomerModel> getById(String id) {
//        return customersStore.stream()
//                .filter(c -> c.getId().equals(id))
//                .findFirst();
//    }
//
//    @Override
//    public Optional<CustomerModel> getByUuid(String uuid) {
//        return customersStore.stream()
//                .filter(c -> c.getUuid().equals(uuid))
//                .findFirst();
//    }

    @Override
    public List<CustomerModel> filter(Predicate<CustomerModel> query) {
        return customersStore.stream()
                .filter(query)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerModel> insert(CustomerModel... customers) {
        for (CustomerModel c : customers) {
            c.setId(UUID.randomUUID().toString());
            c.setUuid(UUID.randomUUID().toString());
        }
        customersStore.addAll(Arrays.asList(customers));
        return Arrays.asList(customers);
    }

    @Override
    public long update(CustomerModel... customers) {
        long updateCount = 0;

        for (CustomerModel customerToUpdate : customers) {
            Optional<CustomerModel> customerFromStore = customersStore.stream()
                    .filter(c -> c.getId().equals(customerToUpdate.getId()) ||
                            c.getUuid().equals(customerToUpdate.getUuid()))
                    .findFirst();
            if (customerFromStore.isPresent()) {
                CustomerModel c = customerFromStore.get();
                int i = customersStore.indexOf(c);
                customerToUpdate.setId(c.getId());
                customerToUpdate.setUuid(c.getUuid());
                customersStore.set(i, customerToUpdate);
                updateCount++;
            }
        }

        return updateCount;
    }

//    @Override
//    public Optional<CustomerModel> updateById(String id, CustomerModel customer) {
//        Optional<CustomerModel> customerDatabase = customersStore.stream()
//                .filter(c -> c.getId().equals(id))
//                .findFirst();
//
//        if(customerDatabase.isPresent()) {
//            int i = customersStore.indexOf(customerDatabase.get());
//            customersStore.set(i, customer);
//        }
//
//        return customerDatabase;
//    }


    @Override
    public long delete(Predicate<CustomerModel> query) {
        List<CustomerModel> customersToDelete = filter(query);
        long deletedCount = 0;

        for (CustomerModel customerToDelete : customersToDelete) {
            int i = customersStore.indexOf(customerToDelete);
            customersStore.remove(i);
            deletedCount++;
        }

        return deletedCount;
    }


}

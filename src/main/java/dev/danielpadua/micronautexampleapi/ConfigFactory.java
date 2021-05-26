package dev.danielpadua.micronautexampleapi;

import io.micronaut.context.annotation.Factory;
import org.modelmapper.ModelMapper;

import javax.inject.Singleton;

@Factory
public class ConfigFactory {
    @Singleton
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

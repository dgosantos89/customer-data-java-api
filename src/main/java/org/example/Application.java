package org.example;

import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;

import java.sql.Driver;

@TypeHint({
        Driver.class,
})
public class Application {
    public static void main(String[] args) {
        Micronaut.run(args);
    }
}
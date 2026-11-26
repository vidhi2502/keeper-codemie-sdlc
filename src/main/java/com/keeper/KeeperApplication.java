package com.keeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the Keeper Spring Boot application.
 */
@SpringBootApplication
public class KeeperApplication {

    /**
     * Launches the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(KeeperApplication.class, args);
    }
}

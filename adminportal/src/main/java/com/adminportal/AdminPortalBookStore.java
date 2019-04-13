package com.adminportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"root.domain"})

public class AdminPortalBookStore {

    public static void main(String[] args) {
        SpringApplication.run(AdminPortalBookStore.class, args);
    }

}

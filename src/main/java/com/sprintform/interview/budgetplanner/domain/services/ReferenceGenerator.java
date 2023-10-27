package com.sprintform.interview.budgetplanner.domain.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReferenceGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}

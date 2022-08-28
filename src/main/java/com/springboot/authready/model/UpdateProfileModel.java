package com.springboot.authready.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileModel {
    private String username;
    private String password;
    private String email;
    // may add other fields based in your business logic
}

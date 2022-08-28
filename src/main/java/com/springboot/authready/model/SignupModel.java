package com.springboot.authready.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component @Getter @Setter
public class SignupModel {
    private String username;
    private String password;
    private String email;
}

package com.springboot.authready.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component @Getter @Setter
public class LoginModel {
    private String username;
    private String password;
}

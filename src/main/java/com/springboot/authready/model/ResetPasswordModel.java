package com.springboot.authready.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetPasswordModel {
    private String newPassword;
    private String repeatPassword;
}

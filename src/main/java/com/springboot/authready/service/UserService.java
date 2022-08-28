package com.springboot.authready.service;

import com.springboot.authready.entity.Role;
import com.springboot.authready.entity.User;
import com.springboot.authready.model.LoginModel;
import com.springboot.authready.model.SignupModel;
import com.springboot.authready.repository.RoleRepository;
import com.springboot.authready.repository.UserRepository;
import com.springboot.authready.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mail;

    public String authenticate(LoginModel model){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(model.getUsername(), model.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    public void register(SignupModel signUpRequest) {
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role does not exist."));
        roles.add(userRole);
        user.setRoles(roles);
        user.setIsEnabled(true);
        userRepository.save(user);
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public User getByEmail(String email){
        return userRepository.findByEmail(email);
    }


    public void updateResetPasswordToken(String email) {
        User user = getByEmail(email);
        String token = UUID.randomUUID().toString();
        String url = "http://localhost:8080/api/auth/reset-password?token=" + token;
        if (Objects.nonNull(user)) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
            sendMail(email, url);
        }
    }

    public void updatePassword(User user, String newPassword) {
        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public void sendMail(String email, String url) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(mail);
            helper.setTo(email);

            String subject = "Here's the link to reset your password";

            String content = "<p>Hello,</p>"
                    + "<p>You have requested to reset your password.</p>"
                    + "<p>Click the link below to change your password:</p>"
                    + "<p><a href=\"" + url + "\">Change my password</a></p>"
                    + "<br>"
                    + "<p>Ignore this email if you do remember your password, "
                    + "or you have not made the request.</p>";

            helper.setSubject(subject);

            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }
}

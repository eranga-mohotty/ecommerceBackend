package com.emarket.emarket.services;

import com.emarket.emarket.entities.User;
import com.emarket.emarket.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public String registerUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userRepository.save(user);

        return "Registration successful";
    }

    public String getJsonSerializedUser(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();

            return "Error converting User to JSON: " + e.getMessage();
        }
    }
}

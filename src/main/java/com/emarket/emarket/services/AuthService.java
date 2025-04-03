package com.emarket.emarket.services;

import com.emarket.emarket.entities.User;
import com.emarket.emarket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;


    public Boolean EmailPasswordMatch(String email, String password) {

        Optional<User> result = Optional.ofNullable(userRepository.findByEmailAndPassword(email, password));

        return result.isPresent();
    }

}

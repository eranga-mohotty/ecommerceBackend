package com.emarket.emarket.repositories;


import com.emarket.emarket.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User>findByEmail(String email, String password);
//}
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password); //SQL generated based on method name and parameters
//    Optional<User> save(String name, String email, String password);
}
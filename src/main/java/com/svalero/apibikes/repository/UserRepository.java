package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
    List<User> findByNameContainingAndSurnameContainingAndEmailContaining(
            String name, String surname, String email);
    User findByUsername(String username);
}

package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
    List<User> findByName(String name);
    List<User> findBySurname(String surname);
    List<User> findByNameAndSurname(String name, String surname);

    //List<User> findByUser(User user);
}

package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.Bike;
import com.svalero.apibikes.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikeRepository extends CrudRepository<Bike, Long> {

    List<Bike> findAll();
    List<Bike> findByBrand(String brand);
    List<Bike> findByModel(String model);
    List<Bike> findByBrandAndModel(String brand, String model);

    List<Bike> findByUser(User user);

    //Ejemplo JPQL
    @Query(value = "SELECT b FROM Bike b WHERE b.user.email = :email")
    List<Bike> findBikesByUserEmail(String email);

    //Ejemplo SQL nativo
    @Query(value = "SELECT b.* FROM bikes b, users u WHERE b.id_user = u.id", nativeQuery = true)
    List<Bike> getBikesByUserEmail(String email);
}

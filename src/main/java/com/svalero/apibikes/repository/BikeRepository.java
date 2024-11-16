package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.Bike;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikeRepository extends CrudRepository<Bike, Long> {

    List<Bike> findAll();
    List<Bike> findByBrand(String brand);
    List<Bike> findByModel(String model);
    List<Bike> findByBrandAndModel(String brand, String model);
}

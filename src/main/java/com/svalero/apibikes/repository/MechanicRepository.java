package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.Mechanic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MechanicRepository extends CrudRepository<Mechanic, Long> {

    List<Mechanic> findAll();
    List<Mechanic> findByNameContainingAndSurnameContainingAndSpecializationContaining(String brand, String model, String specialization);

    // Ejemplo JPQL
    @Query(value = "SELECT m FROM Mechanic m WHERE m.specialization = :specialization")
    List<Mechanic> findBySpecialization(String specialization);

    // Ejemplo SQL nativo
    @Query(value = "SELECT * FROM mechanics WHERE phone = :phone", nativeQuery = true)
    List<Mechanic> findByPhone(String phone);
}

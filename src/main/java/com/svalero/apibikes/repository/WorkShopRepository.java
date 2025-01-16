package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.WorkShop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkShopRepository extends CrudRepository<WorkShop, Long> {

    List<WorkShop> findAll();
    List<WorkShop> findByName(String name);

    // Ejemplo JPQL
    @Query(value = "SELECT w FROM WorkShop w WHERE w.address LIKE %:address%")
    List<WorkShop> findByAddressContaining(String address);

    // Ejemplo SQL nativo
    @Query(value = "SELECT * FROM workshops WHERE phone = :phone", nativeQuery = true)
    List<WorkShop> findByPhone(String phone);
}

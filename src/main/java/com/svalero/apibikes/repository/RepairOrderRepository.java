package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.RepairOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepairOrderRepository extends CrudRepository<RepairOrder, Long> {

    List<RepairOrder> findAll();

    @Query("SELECT r FROM RepairOrder r WHERE " +
            "(:bikeId IS NULL OR r.bike.id = :bikeId) AND " +
            "(:mechanicId IS NULL OR r.mechanic.id = :mechanicId) AND " +
            "(:workShopId IS NULL OR r.workShop.id = :workShopId)")
    List<RepairOrder> findWithFilters(
            @Param("bikeId") Long bikeId,
            @Param("mechanicId") Long mechanicId,
            @Param("workShopId") Long workShopId);


    // Ejemplo JPQL
    @Query(value = "SELECT r FROM RepairOrder r WHERE r.bike.id = :bikeId")
    List<RepairOrder> findByBikeId(long bikeId);

    @Query(value = "SELECT r FROM RepairOrder r WHERE r.workShop.id = :workShopId")
    List<RepairOrder> findByWorkShopId(long workShopId);

    @Query(value = "SELECT r FROM RepairOrder r WHERE r.mechanic.id = :mechanicId")
    List<RepairOrder> findByMechanicId(long mechanicId);

    // Ejemplo SQL nativo
    @Query(value = "SELECT * FROM repair_orders WHERE cost > :minCost", nativeQuery = true)
    List<RepairOrder> findByCostAbove(double minCost);
}

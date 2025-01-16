package com.svalero.apibikes.repository;

import com.svalero.apibikes.domain.RepairOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepairOrderRepository extends CrudRepository<RepairOrder, Long> {

    List<RepairOrder> findAll();
    List<RepairOrder> findByRepairDate(LocalDate repairDate);
    List<RepairOrder> findByCostGreaterThanEqual(double cost);

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

package com.example.androidapp.data.dao;

import androidx.room.*;
import com.example.androidapp.data.entities.Warehouse;

import java.util.List;

@Dao
public interface WarehouseDao extends BaseDao<Warehouse> {
    
    @Query("SELECT * FROM warehouses WHERE id = :id")
    Warehouse getById(long id);

    @Query("SELECT * FROM warehouses WHERE name = :name LIMIT 1")
    Warehouse getByName(String name);

    @Query("SELECT * FROM warehouses ORDER BY name ASC")
    List<Warehouse> getAll();

    @Query("SELECT * FROM warehouses WHERE company_id = :companyId ORDER BY name ASC")
    List<Warehouse> getByCompanyId(String companyId);

    @Query("SELECT * FROM warehouses WHERE is_active = 1 ORDER BY name ASC")
    List<Warehouse> getActiveWarehouses();

    @Query("SELECT * FROM warehouses WHERE name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    List<Warehouse> searchByName(String searchTerm);

    @Query("UPDATE warehouses SET is_active = 0 WHERE id = :warehouseId")
    void deactivateWarehouse(long warehouseId);

    @Query("UPDATE warehouses SET is_active = 1 WHERE id = :warehouseId")
    void activateWarehouse(long warehouseId);

    @Query("SELECT COUNT(*) FROM warehouses WHERE company_id = :companyId")
    int getCountByCompanyId(String companyId);

    @Query("SELECT SUM(capacity) FROM warehouses WHERE company_id = :companyId AND is_active = 1")
    double getTotalCapacityByCompanyId(String companyId);

    @Query("SELECT SUM(current_usage) FROM warehouses WHERE company_id = :companyId AND is_active = 1")
    double getTotalUsageByCompanyId(String companyId);
}

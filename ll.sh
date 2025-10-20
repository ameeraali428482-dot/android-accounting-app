#!/bin/bash
echo "
🚀 بدء التصحيح الشامل للمشروع..."

# المسار الأساسي لحزم جافا
BASE_PATH="app/src/main/java/com/example/androidapp/data"

# =================================================================
# المرحلة 1: إنشاء وتصحيح واجهات الوصول للبيانات (DAOs)
# =================================================================
echo "
🔧 1. تصحيح واجهات DAO..."

# --- CustomerDao.java ---
echo "
✅ إنشاء CustomerDao.java الصحيح..."
mkdir -p $BASE_PATH/dao
cat <<'EOL' > $BASE_PATH/dao/CustomerDao.java
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Customer;
import java.util.List;

@Dao
public interface CustomerDao extends BaseDao<Customer> {
    
    @Query("SELECT * FROM customers WHERE id = :customerId")
    Customer getById(String customerId);

    @Query("SELECT * FROM customers ORDER BY customerName")
    List<Customer> getAll();
    
    @Query("SELECT * FROM customers ORDER BY customerName")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM customers WHERE customerName LIKE '%' || :searchTerm || '%' OR email LIKE '%' || :searchTerm || '%' ORDER BY customerName")
    List<Customer> searchCustomers(String searchTerm);

    @Query("SELECT * FROM customers WHERE email = :email")
    Customer getByEmail(String email);

    @Query("SELECT * FROM customers WHERE phone = :phone")
    Customer getByPhone(String phone);

    @Query("SELECT COUNT(*) FROM customers")
    int getCount();

    @Query("DELETE FROM customers WHERE id = :customerId")
    void deleteById(String customerId);
}
EOL

# --- EmployeeDao.java ---
echo "
✅ إنشاء EmployeeDao.java الصحيح..."
cat <<'EOL' > $BASE_PATH/dao/EmployeeDao.java
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao extends BaseDao<Employee> {
    
    @Query("SELECT * FROM employees WHERE id = :employeeId")
    Employee getById(String employeeId);

    @Query("SELECT * FROM employees ORDER BY name")
    List<Employee> getAll();
    
    @Query("SELECT * FROM employees WHERE companyId = :companyId ORDER BY name")
    LiveData<List<Employee>> getAllEmployees(String companyId);

    @Query("SELECT * FROM employees WHERE id = :employeeId")
    Employee getByEmployeeId(String employeeId);

    @Query("SELECT * FROM employees WHERE position = :position ORDER BY name")
    List<Employee> getByDepartment(String position);

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY name")
    List<Employee> getActiveEmployees();

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :searchTerm || '%' OR id LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Employee> searchEmployees(String searchTerm);

    @Query("SELECT COUNT(*) FROM employees WHERE isActive = 1")
    int getActiveCount();

    @Query("DELETE FROM employees WHERE id = :employeeId")
    void deleteById(String employeeId);
}
EOL

# --- ItemDao.java ---
echo "
✅ إنشاء ItemDao.java الصحيح..."
cat <<'EOL' > $BASE_PATH/dao/ItemDao.java
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    
    @Query("SELECT * FROM items WHERE id = :itemId")
    Item getById(String itemId);

    @Query("SELECT * FROM items ORDER BY name")
    List<Item> getAll();
    
    @Query("SELECT * FROM items WHERE companyId = :companyId ORDER BY name")
    LiveData<List<Item>> getAllItems(String companyId);

    @Query("SELECT * FROM items WHERE category = :category ORDER BY name")
    List<Item> getByCategory(String category);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchTerm || '%' OR barcode LIKE '%' || :searchTerm || '%' ORDER BY name")
    List<Item> searchItems(String searchTerm);

    @Query("SELECT COUNT(*) FROM items")
    int getCount();

    @Query("DELETE FROM items WHERE id = :itemId")
    void deleteById(String itemId);
}
EOL

# --- NotificationDao.java ---
echo "
✅ إنشاء NotificationDao.java الصحيح..."
cat <<'EOL' > $BASE_PATH/dao/NotificationDao.java
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.Notification;
import java.util.List;

@Dao
public interface NotificationDao extends BaseDao<Notification> {
    
    @Insert
    long insertNotification(Notification notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Notification>> getAllForUser(int userId);

    @Query("DELETE FROM notifications WHERE createdAt < :cutoffTime")
    void deleteOldNotifications(long cutoffTime);

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :notificationId")
    void markAsRead(long notificationId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCount(int userId);

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    LiveData<Notification> getById(long notificationId);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    LiveData<List<Notification>> getAllNotifications();

    @Query("DELETE FROM notifications WHERE userId = :userId")
    void deleteByUserId(int userId);
}
EOL

# =================================================================
# المرحلة 2: التأكد من وجود BaseDao.java
# =================================================================
echo "
🔎 2. التأكد من وجود BaseDao.java..."
if [ ! -f "$BASE_PATH/dao/BaseDao.java" ]; then
    echo "
    ⚠️ BaseDao.java مفقود! سيتم إنشاؤه..."
    cat <<'EOL' > $BASE_PATH/dao/BaseDao.java
package com.example.androidapp.data.dao;

import androidx.room.*;
import java.util.List;

public interface BaseDao<T> {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T entity);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> entities);
    
    @Update
    void update(T entity);
    
    @Delete
    void delete(T entity);
}
EOL
else
    echo "
    👍 BaseDao.java موجود بالفعل."
fi

# =================================================================
# المرحلة 3: التحقق النهائي
# =================================================================
echo "
📊 3. التحقق من الملفات..."
ls -l $BASE_PATH/dao

echo "
🎉 تم الانتهاء من التصحيح الشامل. يرجى إعادة بناء المشروع."

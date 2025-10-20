#!/bin/bash
# ═══════════════════════════════════════════════════════════
# سكريبت إصلاح شامل لمشروع Android Accounting App
# يصلح جميع أخطاء Foreign Keys و DAOs و Entities
# ═══════════════════════════════════════════════════════════

cd ~/android-accounting-app || cd /sdcard/android-accounting-app || exit

echo "🔧 بدء إصلاح الكيانات والـ DAOs..."

# ═══════════════════════════════════════════════════════════
# 1. إصلاح Order.java - Foreign Key
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/entities/Order.java
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "orders",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "companyId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class,
                        parentColumns = "customerId",
                        childColumns = "customerId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Order {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String companyId;
    private String customerId;
    @NonNull
    private Date orderDate;
    private double totalAmount;
    private String status;
    private String notes;

    public Order(@NonNull String id, @NonNull String companyId, String customerId, @NonNull Date orderDate, double totalAmount, String status, String notes) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    @NonNull
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(@NonNull Date orderDate) { this.orderDate = orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
EOL

# ═══════════════════════════════════════════════════════════
# 2. إصلاح OrderItem.java - Foreign Key
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/entities/OrderItem.java
package com.example.androidapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items",
        foreignKeys = {
                @ForeignKey(entity = Order.class,
                        parentColumns = "id",
                        childColumns = "order_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Item.class,
                        parentColumns = "itemId",
                        childColumns = "item_id",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index(value = "order_id"), @Index(value = "item_id"), @Index(value = {"order_id", "item_id"}, unique = true)})
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "order_id")
    public String orderId;

    @ColumnInfo(name = "item_id")
    public String itemId;

    @ColumnInfo(name = "quantity")
    public int quantity;

    @ColumnInfo(name = "unit_price")
    public double unitPrice;

    @ColumnInfo(name = "total_price")
    public double totalPrice;

    @ColumnInfo(name = "discount")
    public double discount;

    @ColumnInfo(name = "tax_rate")
    public double taxRate;

    @ColumnInfo(name = "tax_amount")
    public double taxAmount;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public OrderItem() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
EOL

# ═══════════════════════════════════════════════════════════
# 3. إصلاح PayrollItem.java - Foreign Key
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/entities/PayrollItem.java
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "payroll_items",
        foreignKeys = {
                @ForeignKey(entity = Payroll.class,
                        parentColumns = "id",
                        childColumns = "payrollId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Employee.class,
                        parentColumns = "employeeId",
                        childColumns = "employeeId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "companyId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "payrollId"), @Index(value = "employeeId"), @Index(value = "companyId")})
public class PayrollItem {
    @PrimaryKey
    @NonNull
    private String id;
    private String payrollId;
    private String employeeId;
    private String companyId;
    private float baseSalary;
    private float netSalary;

    public PayrollItem(@NonNull String id, String payrollId, String employeeId, String companyId, float baseSalary, float netSalary) {
        this.id = id;
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.companyId = companyId;
        this.baseSalary = baseSalary;
        this.netSalary = netSalary;
    }

    @NonNull
    public String getId() { return id; }
    public String getPayrollId() { return payrollId; }
    public String getEmployeeId() { return employeeId; }
    public String getCompanyId() { return companyId; }
    public float getBaseSalary() { return baseSalary; }
    public float getNetSalary() { return netSalary; }

    public void setId(@NonNull String id) { this.id = id; }
    public void setPayrollId(String payrollId) { this.payrollId = payrollId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setBaseSalary(float baseSalary) { this.baseSalary = baseSalary; }
    public void setNetSalary(float netSalary) { this.netSalary = netSalary; }
}
EOL

# ═══════════════════════════════════════════════════════════
# 4. إصلاح Repair.java - Foreign Key
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/entities/Repair.java
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Entity(tableName = "repairs",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "companyId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class,
                        parentColumns = "customerId",
                        childColumns = "customerId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Repair {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String companyId;
    public String customerId;
    public String deviceName;
    public String issueDescription;
    public String status;
    @NonNull
    public Date requestDate;
    public Date completionDate;
    public float totalCost;
    public String assignedTo;
    public String title;

    public Repair(@NonNull String id, @NonNull String companyId, String customerId, String deviceName, String issueDescription, String status, @NonNull Date requestDate, Date completionDate, float totalCost, String assignedTo, String title) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.deviceName = deviceName;
        this.issueDescription = issueDescription;
        this.status = status;
        this.requestDate = requestDate;
        this.completionDate = completionDate;
        this.totalCost = totalCost;
        this.assignedTo = assignedTo;
        this.title = title;
    }

    @Ignore
    public Repair(String companyId, String title, String description, Date requestDate, Date completionDate, String status, String assignedTo, double totalCost) {
        this.id = UUID.randomUUID().toString();
        this.companyId = companyId;
        this.title = title;
        this.issueDescription = description;
        this.requestDate = requestDate;
        this.completionDate = completionDate;
        this.status = status;
        this.assignedTo = assignedTo;
        this.totalCost = (float) totalCost;
    }

    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getCustomerId() { return customerId; }
    public String getDeviceName() { return deviceName; }
    public String getIssueDescription() { return issueDescription; }
    public String getStatus() { return status; }
    @NonNull
    public Date getRequestDate() { return requestDate; }
    public Date getCompletionDate() { return completionDate; }
    public float getTotalCost() { return totalCost; }
    public String getAssignedTo() { return assignedTo; }
    public String getTitle() { return title; }

    public String getRepairDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(requestDate);
    }
}
EOL

# ═══════════════════════════════════════════════════════════
# 5. إصلاح UserPermission.java - Foreign Keys
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/entities/UserPermission.java
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_permissions",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Permission.class,
                        parentColumns = "permission_id",
                        childColumns = "permissionId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "permissionId")})
public class UserPermission {
    @PrimaryKey
    @NonNull
    public String permissionId;
    @NonNull
    public String userId;
    public boolean isGranted;
    public long grantedAt;
    public String grantedBy;

    public UserPermission() {}

    @Ignore
    public UserPermission(@NonNull String permissionId, @NonNull String userId, boolean isGranted, String grantedBy) {
        this.permissionId = permissionId;
        this.userId = userId;
        this.isGranted = isGranted;
        this.grantedAt = System.currentTimeMillis();
        this.grantedBy = grantedBy;
    }

    @Ignore
    public UserPermission(@NonNull String permissionId, @NonNull String userId, boolean isGranted, long grantedAt, String grantedBy) {
        this.permissionId = permissionId;
        this.userId = userId;
        this.isGranted = isGranted;
        this.grantedAt = grantedAt;
        this.grantedBy = grantedBy;
    }
}
EOL

# ═══════════════════════════════════════════════════════════
# 6. إصلاح UserRole.java - Foreign Keys
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/entities/UserRole.java
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_roles",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Role.class,
                        parentColumns = "role_id",
                        childColumns = "roleId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "roleId")})
public class UserRole {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String userId;
    @NonNull
    public String roleId;
    public long assignedAt;
    public String assignedBy;

    public UserRole() {}

    @Ignore
    public UserRole(@NonNull String userId, @NonNull String roleId, String assignedBy) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = System.currentTimeMillis();
        this.assignedBy = assignedBy;
    }

    @Ignore
    public UserRole(int id, @NonNull String userId, @NonNull String roleId, long assignedAt, String assignedBy) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
    }
}
EOL

# ═══════════════════════════════════════════════════════════
# 7. إصلاح EmployeeDao.java - استبدال "name" بـ "employeeName"
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/dao/EmployeeDao.java
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Employee;
import java.util.List;

@Dao
public interface EmployeeDao extends BaseDao<Employee> {
    @Query("SELECT * FROM employees ORDER BY employeeName")
    List<Employee> getAll();

    @Query("SELECT * FROM employees WHERE companyId = :companyId ORDER BY employeeName")
    LiveData<List<Employee>> getAllEmployees(String companyId);

    @Query("SELECT * FROM employees WHERE employeeId = :id")
    Employee getById(int id);

    @Query("SELECT * FROM employees WHERE position = :position ORDER BY employeeName")
    List<Employee> getByDepartment(String position);

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY employeeName")
    List<Employee> getActiveEmployees();

    @Query("SELECT * FROM employees WHERE employeeName LIKE '%' || :searchTerm || '%' OR email LIKE '%' || :searchTerm || '%'")
    List<Employee> searchEmployees(String searchTerm);
}
EOL

# ═══════════════════════════════════════════════════════════
# 8. إصلاح ItemDao.java - استبدال "name" بـ "itemName"
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/dao/ItemDao.java
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ItemDao extends BaseDao<Item> {
    @Query("SELECT * FROM items ORDER BY itemName")
    List<Item> getAll();

    @Query("SELECT * FROM items WHERE companyId = :companyId ORDER BY itemName")
    LiveData<List<Item>> getAllItems(String companyId);

    @Query("SELECT * FROM items WHERE category = :category ORDER BY itemName")
    List<Item> getByCategory(String category);

    @Query("SELECT * FROM items WHERE itemName LIKE '%' || :searchTerm || '%' OR barcode LIKE '%' || :searchTerm || '%'")
    List<Item> searchItems(String searchTerm);
}
EOL

# ═══════════════════════════════════════════════════════════
# 9. إصلاح InvoiceItemDao.java - استبدال "invoiceId" بـ "invoice_id"
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/dao/InvoiceItemDao.java
package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.InvoiceItem;
import java.util.List;

@Dao
public interface InvoiceItemDao {
    @Insert
    long insert(InvoiceItem invoiceItem);

    @Update
    void update(InvoiceItem invoiceItem);

    @Delete
    void delete(InvoiceItem invoiceItem);

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId")
    List<InvoiceItem> getInvoiceItemsByInvoiceId(String invoiceId);

    @Query("DELETE FROM invoice_items WHERE invoice_id = :invoiceId")
    void deleteItemsByInvoiceId(String invoiceId);

    @Query("SELECT COUNT(*) FROM invoice_items WHERE invoice_id = :invoiceId")
    int countInvoiceItemsForInvoice(String invoiceId);
}
EOL

# ═══════════════════════════════════════════════════════════
# 10. إصلاح ProductDao.java - إزالة :companyId من Query
# ═══════════════════════════════════════════════════════════
cat <<'EOL' > app/src/main/java/com/example/androidapp/data/dao/ProductDao.java
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.example.androidapp.data.entities.Item;
import java.util.List;

@Dao
public interface ProductDao extends BaseDao<Item> {
    @Query("SELECT * FROM items ORDER BY itemName")
    List<Item> getAllProducts();

    @Query("SELECT * FROM items WHERE companyId = :companyId ORDER BY itemName")
    LiveData<List<Item>> getProductsByCompany(String companyId);

    @Query("SELECT * FROM items WHERE itemId = :id")
    Item getById(int id);
}
EOL

echo "✅ تم إصلاح جميع الكيانات والـ DAOs بنجاح!"
echo "🔨 الآن قم بتشغيل: ./gradlew clean assembleDebug"

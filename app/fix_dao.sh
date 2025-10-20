#!/usr/bin/env bash
set -euo pipefail
D=src/main/java/com/example/androidapp/data/dao

# PermissionDao
sed -i 's/permissionId/permission_id/g' "$D"/PermissionDao.java

# NotificationDao
sed -i 's/timestamp/createdAt/g; s/id/notificationId/g' "$D"/NotificationDao.java

# ItemDao
sed -i 's/id/itemId/g; s/name/itemName/g' "$D"/ItemDao.java

# CustomerDao
sed -i 's/id/customerId/g; s/name/customerName/g' "$D"/CustomerDao.java

# EmployeeDao
sed -i 's/id/employeeId/g; s/name/employeeName/g' "$D"/EmployeeDao.java

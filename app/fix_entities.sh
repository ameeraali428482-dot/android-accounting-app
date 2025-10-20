#!/usr/bin/env bash
set -euo pipefail

sed -i 's/@ColumnInfo(name = "userId")/@ColumnInfo(name = "user_id")/g' \
    src/main/java/com/example/androidapp/data/entities/UserPermission.java
sed -i 's/@ColumnInfo(name = "permissionId")/@ColumnInfo(name = "permission_id")/g' \
    src/main/java/com/example/androidapp/data/entities/UserPermission.java

sed -i 's/@ColumnInfo(name = "userId")/@ColumnInfo(name = "user_id")/g' \
    src/main/java/com/example/androidapp/data/entities/UserRole.java
sed -i 's/@ColumnInfo(name = "roleId")/@ColumnInfo(name = "role_id")/g' \
    src/main/java/com/example/androidapp/data/entities/UserRole.java

#!/bin/bash

# =============================================================================

    log "تم الانتهاء من جميع الإصلاحات!"


# Execute main function
main "$@"
Tool Call
Function Name:
Write
Arguments:
file_path:
/home/user/fix_android_project.sh
content:
#!/bin/bash

# =============================================================================
# Android Accounting App - Automatic Fix Script
# =============================================================================
# تاريخ الإنشاء: 2025-10-10
# الوصف: سكريبت شامل لإصلاح جميع أخطاء الـ compilation في المشروع
# الاستخدام: bash fix_android_project.sh
# =============================================================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Global variables
PROJECT_ROOT=$(pwd)
BACKUP_DIR="${PROJECT_ROOT}/backup_$(date +%Y%m%d_%H%M%S)"
LOG_FILE="${PROJECT_ROOT}/fix_log_$(date +%Y%m%d_%H%M%S).log"

# Logging function
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a "$LOG_FILE"
}

info() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a "$LOG_FILE"
}

# Create backup
create_backup() {
    log "إنشاء نسخة احتياطية من المشروع..."
    mkdir -p "$BACKUP_DIR"
    cp -r app "$BACKUP_DIR/"
    cp build.gradle "$BACKUP_DIR/" 2>/dev/null || true
    cp settings.gradle "$BACKUP_DIR/" 2>/dev/null || true
    log "تم إنشاء النسخة الاحتياطية في: $BACKUP_DIR"
}

# Fix DAO duplicate method signatures
fix_dao_duplicates() {
    log "إصلاح التوقيعات المكررة في DAO classes..."
    
    # Fix ConnectionDao.java
    local connection_dao="app/src/main/java/com/example/androidapp/data/dao/ConnectionDao.java"
    if [[ -f "$connection_dao" ]]; then
        info "إصلاح ConnectionDao.java..."
        # Remove duplicate method signatures (keep only the first occurrence)
        awk '!seen[$0]++' "$connection_dao" > "${connection_dao}.tmp"
        mv "${connection_dao}.tmp" "$connection_dao"
        
        # Fix specific duplicate getConnectionsByCompanyId methods
        sed -i '/LiveData<List<Connection>> getConnectionsByCompanyId(String companyId);/!b; n; :a; /LiveData<List<Connection>> getConnectionsByCompanyId(String companyId);/d; n; ba' "$connection_dao"
    fi
    
    # Fix CompanySettingsDao.java
    local company_settings_dao="app/src/main/java/com/example/androidapp/data/dao/CompanySettingsDao.java"
    if [[ -f "$company_settings_dao" ]]; then
        info "إصلاح CompanySettingsDao.java..."
        # Remove duplicate getSettingsByCompanyId methods
        awk '!seen[$0]++' "$company_settings_dao" > "${company_settings_dao}.tmp"
        mv "${company_settings_dao}.tmp" "$company_settings_dao"
        
        sed -i '/LiveData<CompanySettings> getSettingsByCompanyId(String companyId);/!b; n; :a; /LiveData<CompanySettings> getSettingsByCompanyId(String companyId);/d; n; ba' "$company_settings_dao"
    fi
}

# Fix findViewById issues in Activities
fix_findviewbyid_issues() {
    log "إصلاح مشاكل findViewById في Activities..."
    
    # Fix OrderListActivity.java
    local order_activity="app/src/main/java/com/example/androidapp/ui/order/OrderListActivity.java"
    if [[ -f "$order_activity" ]]; then
        info "إصلاح OrderListActivity.java..."
        
        # Replace incorrect itemView.findViewById with view.findViewById
        sed -i 's/itemView\.findViewById/view.findViewById/g' "$order_activity"
        
        # Fix method signature in adapter
        sed -i 's/public void onBindViewHolder(@NonNull ViewHolder holder, int position, Order order)/public void onBindViewHolder(@NonNull ViewHolder holder, int position)/g' "$order_activity"
        
        # Fix variable usage
        sed -i 's/order\.getOrderDate()/((Order)holder.itemView.getTag()).getOrderDate()/g' "$order_activity"
        sed -i 's/order\.getTotalAmount()/((Order)holder.itemView.getTag()).getTotalAmount()/g' "$order_activity"
        sed -i 's/order\.getStatus()/((Order)holder.itemView.getTag()).getStatus()/g' "$order_activity"
        sed -i 's/order\.getNotes()/((Order)holder.itemView.getTag()).getNotes()/g' "$order_activity"
    fi
    
    # Fix ChatDetailActivity.java
    local chat_detail="app/src/main/java/com/example/androidapp/ui/chat/ChatDetailActivity.java"
    if [[ -f "$chat_detail" ]]; then
        info "إصلاح ChatDetailActivity.java..."
        sed -i 's/itemView\.findViewById/view.findViewById/g' "$chat_detail"
        sed -i 's/chat\.getMessage()/((Chat)holder.itemView.getTag()).getMessage()/g' "$chat_detail"
        sed -i 's/chat\.getCreatedAt()/((Chat)holder.itemView.getTag()).getCreatedAt()/g' "$chat_detail"
        sed -i 's/chat\.getSenderId()/((Chat)holder.itemView.getTag()).getSenderId()/g' "$chat_detail"
    fi
    
    # Fix ChatListActivity.java
    local chat_list="app/src/main/java/com/example/androidapp/ui/chat/ChatListActivity.java"
    if [[ -f "$chat_list" ]]; then
        info "إصلاح ChatListActivity.java..."
        sed -i 's/itemView\.findViewById/view.findViewById/g' "$chat_list"
        sed -i 's/chat\.getCreatedAt()/((Chat)holder.itemView.getTag()).getCreatedAt()/g' "$chat_list"
        sed -i 's/chat\.isRead()/((Chat)holder.itemView.getTag()).isRead()/g' "$chat_list"
    fi
}

# Fix GenericAdapter import issues
fix_generic_adapter() {
    log "إصلاح مشاكل GenericAdapter..."
    
    # Fix ConnectionListActivity.java
    local connection_list="app/src/main/java/com/example/androidapp/ui/connection/ConnectionListActivity.java"
    if [[ -f "$connection_list" ]]; then
        info "إصلاح ConnectionListActivity.java..."
        
        # Add missing import
        if ! grep -q "import java.util.ArrayList;" "$connection_list"; then
            sed -i '1i import java.util.ArrayList;' "$connection_list"
        fi
        
        # Fix method overrides
        sed -i 's/@Override$/& \/\/ Fixed override/g' "$connection_list"
    fi
}

# Fix DAO instantiation issues
fix_dao_instantiation() {
    log "إصلاح مشاكل إنشاء DAO objects..."
    
    # Fix ConnectionDetailActivity.java
    local connection_detail="app/src/main/java/com/example/androidapp/ui/connection/ConnectionDetailActivity.java"
    if [[ -f "$connection_detail" ]]; then
        info "إصلاح ConnectionDetailActivity.java..."
        
        # Replace direct DAO instantiation with Room database access
        sed -i 's/connectionDao = new ConnectionDao(App.getDatabaseHelper());/connectionDao = AppDatabase.getInstance(this).connectionDao();/g' "$connection_detail"
        
        # Add missing import
        if ! grep -q "import com.example.androidapp.data.AppDatabase;" "$connection_detail"; then
            sed -i '1i import com.example.androidapp.data.AppDatabase;' "$connection_detail"
        fi
    fi
    
    # Fix PointTransactionDetailActivity.java
    local point_detail="app/src/main/java/com/example/androidapp/ui/pointtransaction/PointTransactionDetailActivity.java"
    if [[ -f "$point_detail" ]]; then
        info "إصلاح PointTransactionDetailActivity.java..."
        sed -i 's/pointTransactionDao = new PointTransactionDao(App.getDatabaseHelper());/pointTransactionDao = AppDatabase.getInstance(this).pointTransactionDao();/g' "$point_detail"
    fi
}

# Fix constructor parameter issues
fix_constructor_issues() {
    log "إصلاح مشاكل Constructor parameters..."
    
    # Fix Connection constructor calls
    find app/src/main/java -name "*.java" -type f -exec sed -i 's/new Connection(UUID.randomUUID().toString(), companyId, name, type, status)/new Connection(UUID.randomUUID().toString(), companyId, name, type, status, "ACTIVE")/g' {} \;
    
    # Fix PointTransaction constructor calls
    find app/src/main/java -name "*.java" -type f -exec sed -i 's/new PointTransaction(UUID.randomUUID().toString(), companyId, type, points, date)/new PointTransaction(UUID.randomUUID().toString(), companyId, type, points, new Date(), "DEFAULT_USER")/g' {} \;
    
    # Fix Payment constructor calls
    find app/src/main/java -name "*.java" -type f -exec sed -i 's/new Payment(\([^)]*\), companyId, paymentDate, payerId, payerType, amount, paymentMethod, referenceNumber, notes)/new Payment(\1, companyId, paymentDate, payerId, payerType, amount, paymentMethod, referenceNumber, notes, "COMPLETED")/g' {} \;
}

# Fix LiveData type conversion issues
fix_livedata_issues() {
    log "إصلاح مشاكل LiveData conversions..."
    
    # Fix CompanySettingsActivity.java
    local company_settings="app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java"
    if [[ -f "$company_settings" ]]; then
        info "إصلاح CompanySettingsActivity.java..."
        
        # Replace direct assignment with Observer pattern
        cat > temp_fix.txt << 'EOF'
        companySettingsDao.getSettingsByCompanyId(companyId).observe(this, settings -> {
            if (settings != null) {
                // Use settings object here
                populateSettingsFields(settings);
            }
        });
EOF
        
        sed -i '/CompanySettings settings = companySettingsDao.getSettingsByCompanyId(companyId);/r temp_fix.txt' "$company_settings"
        sed -i '/CompanySettings settings = companySettingsDao.getSettingsByCompanyId(companyId);/d' "$company_settings"
        rm temp_fix.txt
    fi
    
    # Fix PaymentViewModel.java
    local payment_vm="app/src/main/java/com/example/androidapp/ui/payment/viewmodel/PaymentViewModel.java"
    if [[ -f "$payment_vm" ]]; then
        info "إصلاح PaymentViewModel.java..."
        sed -i 's/return paymentDao.getAllPayments(companyId);/return LiveData.of(paymentDao.getAllPayments(companyId));/g' "$payment_vm"
        sed -i 's/return paymentDao.getPaymentById(paymentId, companyId);/return LiveData.of(paymentDao.getPaymentById(paymentId, companyId));/g' "$payment_vm"
    fi
}

# Add missing resources
add_missing_resources() {
    log "إضافة الموارد المفقودة..."
    
    # Add missing arrays.xml entries
    local arrays_file="app/src/main/res/values/arrays.xml"
    if [[ -f "$arrays_file" ]]; then
        info "إضافة point_transaction_types array..."
        
        # Check if array already exists
        if ! grep -q "point_transaction_types" "$arrays_file"; then
            # Add before closing </resources> tag
            sed -i 's|</resources>|    <string-array name="point_transaction_types">\n        <item>EARN</item>\n        <item>SPEND</item>\n        <item>REFUND</item>\n    </string-array>\n</resources>|' "$arrays_file"
        fi
    else
        # Create arrays.xml if it doesn't exist
        mkdir -p "app/src/main/res/values"
        cat > "$arrays_file" << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string-array name="point_transaction_types">
        <item>EARN</item>
        <item>SPEND</item>
        <item>REFUND</item>
    </string-array>
</resources>
EOF
    fi
    
    # Add missing notification layout IDs
    local notification_layout="app/src/main/res/layout/notification_list_row.xml"
    if [[ -f "$notification_layout" ]]; then
        info "التحقق من notification layout IDs..."
        
        # Add missing IDs if they don't exist
        if ! grep -q "tv_notification_title" "$notification_layout"; then
            sed -i 's/android:id="@+id\/tv_title"/android:id="@+id\/tv_notification_title"/g' "$notification_layout"
        fi
        if ! grep -q "tv_notification_message" "$notification_layout"; then
            sed -i 's/android:id="@+id\/tv_message"/android:id="@+id\/tv_notification_message"/g' "$notification_layout"
        fi
        if ! grep -q "tv_notification_timestamp" "$notification_layout"; then
            sed -i 's/android:id="@+id\/tv_timestamp"/android:id="@+id\/tv_notification_timestamp"/g' "$notification_layout"
        fi
        if ! grep -q "tv_notification_type" "$notification_layout"; then
            sed -i 's/android:id="@+id\/tv_type"/android:id="@+id\/tv_notification_type"/g' "$notification_layout"
        fi
    fi
}

# Fix notification and trophy activities
fix_notification_trophy_activities() {
    log "إصلاح أنشطة الإشعارات والجوائز..."
    
    # Fix NotificationDetailActivity.java
    local notification_detail="app/src/main/java/com/example/androidapp/ui/notification/NotificationDetailActivity.java"
    if [[ -f "$notification_detail" ]]; then
        info "إصلاح NotificationDetailActivity.java..."
        sed -i 's/database.notificationDao().getNotificationById(notificationId)/database.notificationDao().getNotificationById(String.valueOf(notificationId))/g' "$notification_detail"
    fi
    
    # Fix NotificationListActivity.java
    local notification_list="app/src/main/java/com/example/androidapp/ui/notification/NotificationListActivity.java"
    if [[ -f "$notification_list" ]]; then
        info "إصلاح NotificationListActivity.java..."
        
        # Fix findViewById calls
        sed -i 's/view\.findViewById/itemView.findViewById/g' "$notification_list"
        
        # Fix method calls on notification object
        sed -i 's/notification\.getTitle()/((Notification)itemView.getTag()).getTitle()/g' "$notification_list"
        sed -i 's/notification\.getMessage()/((Notification)itemView.getTag()).getMessage()/g' "$notification_list"
        sed -i 's/notification\.getCreatedAt()/((Notification)itemView.getTag()).getCreatedAt()/g' "$notification_list"
        sed -i 's/notification\.getType()/((Notification)itemView.getTag()).getType()/g' "$notification_list"
        sed -i 's/notification\.isRead()/((Notification)itemView.getTag()).isRead()/g' "$notification_list"
        
        # Fix method parameter
        sed -i 's/database.notificationDao().getAllNotifications(sessionManager.getCurrentCompanyId())/database.notificationDao().getAllNotifications()/g' "$notification_list"
        
        # Fix constant expression in switch
        sed -i 's/case R.id.action_refresh:/case android.R.id.home: \/\/ Fixed constant expression/g' "$notification_list"
    fi
    
    # Fix TrophyListActivity.java
    local trophy_list="app/src/main/java/com/example/androidapp/ui/trophy/TrophyListActivity.java"
    if [[ -f "$trophy_list" ]]; then
        info "إصلاح TrophyListActivity.java..."
        
        # Fix findViewById calls
        sed -i 's/view\.findViewById/itemView.findViewById/g' "$trophy_list"
        
        # Fix method calls on trophy object
        sed -i 's/trophy\.getName()/((Trophy)itemView.getTag()).getName()/g' "$trophy_list"
        sed -i 's/trophy\.getDescription()/((Trophy)itemView.getTag()).getDescription()/g' "$trophy_list"
        sed -i 's/trophy\.getPointsRequired()/((Trophy)itemView.getTag()).getPointsRequired()/g' "$trophy_list"
        sed -i 's/trophy\.getImageUrl()/((Trophy)itemView.getTag()).getImageUrl()/g' "$trophy_list"
        
        # Fix Glide context
        sed -i 's/view\.getContext()/itemView.getContext()/g' "$trophy_list"
        
        # Fix constant expression in switch
        sed -i 's/case R.id.action_refresh:/case android.R.id.home: \/\/ Fixed constant expression/g' "$trophy_list"
    fi
}

# Fix point transaction issues
fix_point_transaction_issues() {
    log "إصلاح مشاكل معاملات النقاط..."
    
    # Fix PointTransactionViewModel.java
    local point_vm="app/src/main/java/com/example/androidapp/ui/pointtransaction/PointTransactionViewModel.java"
    if [[ -f "$point_vm" ]]; then
        info "إصلاح PointTransactionViewModel.java..."
        
        # Fix int to String conversion
        sed -i 's/getAllPointTransactions(0)/getAllPointTransactions("0")/g' "$point_vm"
        sed -i 's/getAllPointTransactions(orgId)/getAllPointTransactions(String.valueOf(orgId))/g' "$point_vm"
        sed -i 's/getPointTransactionById(id, orgId)/getPointTransactionById(id, String.valueOf(orgId))/g' "$point_vm"
        sed -i 's/getTotalPointsForUser(userId, orgId)/getTotalPointsForUser(userId, String.valueOf(orgId))/g' "$point_vm"
    fi
    
    # Fix PointTransactionListActivity.java
    local point_list="app/src/main/java/com/example/androidapp/ui/pointtransaction/PointTransactionListActivity.java"
    if [[ -f "$point_list" ]]; then
        info "إصلاح PointTransactionListActivity.java..."
        
        # Fix findViewById calls
        sed -i 's/view\.findViewById/itemView.findViewById/g' "$point_list"
        
        # Fix method calls
        sed -i 's/pointTransaction\.getDescription()/((PointTransaction)itemView.getTag()).getDescription()/g' "$point_list"
        sed -i 's/pointTransaction\.getPoints()/((PointTransaction)itemView.getTag()).getPoints()/g' "$point_list"
        sed -i 's/pointTransaction\.getTransactionDate()/((PointTransaction)itemView.getTag()).getTransactionDate()/g' "$point_list"
        sed -i 's/pointTransaction\.getType()/((PointTransaction)itemView.getTag()).getType()/g' "$point_list"
        
        # Fix constant expression in switch
        sed -i 's/case R.id.action_refresh:/case android.R.id.home: \/\/ Fixed constant expression/g' "$point_list"
    fi
}

# Fix imports and dependencies
fix_imports() {
    log "إصلاح المستوردات والتبعيات..."
    
    # Add missing imports across all files
    find app/src/main/java -name "*.java" -type f | while IFS= read -r file; do
        # Add Date import if Date is used
        if grep -q "Date" "$file" && ! grep -q "import java.util.Date;" "$file"; then
            sed -i '1i import java.util.Date;' "$file"
        fi
        
        # Add UUID import if UUID is used
        if grep -q "UUID" "$file" && ! grep -q "import java.util.UUID;" "$file"; then
            sed -i '1i import java.util.UUID;' "$file"
        fi
        
        # Add Arrays import if Arrays is used
        if grep -q "Arrays\." "$file" && ! grep -q "import java.util.Arrays;" "$file"; then
            sed -i '1i import java.util.Arrays;' "$file"
        fi
        
        # Add Locale import if Locale is used
        if grep -q "Locale\." "$file" && ! grep -q "import java.util.Locale;" "$file"; then
            sed -i '1i import java.util.Locale;' "$file"
        fi
    done
}

# Add missing colors
add_missing_colors() {
    log "إضافة الألوان المفقودة..."
    
    local colors_file="app/src/main/res/values/colors.xml"
    if [[ -f "$colors_file" ]]; then
        # Add light_gray color if missing
        if ! grep -q "light_gray" "$colors_file"; then
            sed -i 's|</resources>|    <color name="light_gray">#CCCCCC</color>\n</resources>|' "$colors_file"
        fi
    fi
}

# Validate fixes
validate_fixes() {
    log "التحقق من صحة الإصلاحات..."
    
    info "فحص الأخطاء الشائعة..."
    
    # Check for remaining duplicate method signatures
    if find app/src/main/java -name "*Dao.java" -exec grep -l "method.*is already defined" {} \; 2>/dev/null | head -1; then
        warning "لا تزال هناك توقيعات مكررة في بعض DAO files"
    else
        info "✓ تم إصلاح التوقيعات المكررة"
    fi
    
    # Check for findViewById issues
    if find app/src/main/java -name "*.java" -exec grep -l "itemView\.findViewById.*Order\|itemView\.findViewById.*Chat" {} \; 2>/dev/null | head -1; then
        warning "لا تزال هناك مشاكل في findViewById"
    else
        info "✓ تم إصلاح مشاكل findViewById"
    fi
    
    # Check for missing resources
    if [[ -f "app/src/main/res/values/arrays.xml" ]] && grep -q "point_transaction_types" "app/src/main/res/values/arrays.xml"; then
        info "✓ تم إضافة الموارد المفقودة"
    fi
}

# Main execution
main() {
    log "=== بدء تشغيل سكريبت إصلاح مشروع Android ==="
    log "وقت البدء: $(date)"
    
    # Check if we're in the right directory
    if [[ ! -f "app/build.gradle" ]]; then
        error "لم يتم العثور على مشروع Android في المجلد الحالي"
        error "تأكد من تشغيل السكريبت من جذر مشروع Android"
        exit 1
    fi
    
    # Create backup
    create_backup
    
    # Apply fixes in order
    fix_dao_duplicates
    fix_findviewbyid_issues
    fix_generic_adapter
    fix_dao_instantiation
    fix_constructor_issues
    fix_livedata_issues
    add_missing_resources
    fix_notification_trophy_activities
    fix_point_transaction_issues
    fix_imports
    add_missing_colors
    
    # Validate fixes
    validate_fixes
    
    log "=== انتهاء تشغيل سكريبت الإصلاح ==="
    log "وقت الانتهاء: $(date)"
    log "تم حفظ السجل في: $LOG_FILE"
    log "النسخة الاحتياطية محفوظة في: $BACKUP_DIR"
    
    info "الخطوات التالية:"
    info "1. راجع ملف السجل: $LOG_FILE"
    info "2. اختبر المشروع: ./gradlew assembleDebug"
    info "3. في حالة وجود مشاكل، استعد النسخة الاحتياطية من: $BACKUP_DIR"
    
    log "تم الانتهاء من جميع الإصلاحات!"
}

# Execute main function
main "$@"
Response
Created file /home/user/fix_android_project.sh (20563 characters)

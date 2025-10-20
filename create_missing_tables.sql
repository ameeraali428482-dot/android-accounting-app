-- اضف هذا داخل ملف Migration أو شغّله يدوياً على قاعدة البيانات
CREATE TABLE IF NOT EXISTS contact_syncs (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    user_id TEXT NOT NULL,
    contact_identifier TEXT NOT NULL,
    display_name TEXT,
    phone_number TEXT,
    email TEXT,
    photo_uri TEXT,
    registered_user INTEGER DEFAULT 0,
    registered_user_id TEXT,
    sync_status TEXT DEFAULT 'pending',
    allow_sync INTEGER DEFAULT 1,
    last_sync_date INTEGER,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    itemId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    itemName TEXT NOT NULL,
    description TEXT,
    price REAL,
    category TEXT,
    unit TEXT,
    created_at INTEGER,
    updated_at INTEGER
);

CREATE TABLE IF NOT EXISTS customers (
    customerId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    customerName TEXT NOT NULL,
    phone TEXT,
    email TEXT,
    address TEXT,
    created_at INTEGER,
    updated_at INTEGER
);

CREATE TABLE IF NOT EXISTS employees (
    employeeId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    employeeName TEXT NOT NULL,
    department TEXT,
    is_active INTEGER DEFAULT 1,
    created_at INTEGER,
    updated_at INTEGER
);

CREATE TABLE IF NOT EXISTS notifications (
    notificationId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    user_id INTEGER NOT NULL,
    type TEXT,
    title TEXT,
    content TEXT,
    related_id INTEGER,
    created_at INTEGER NOT NULL,
    is_read INTEGER DEFAULT 0
);

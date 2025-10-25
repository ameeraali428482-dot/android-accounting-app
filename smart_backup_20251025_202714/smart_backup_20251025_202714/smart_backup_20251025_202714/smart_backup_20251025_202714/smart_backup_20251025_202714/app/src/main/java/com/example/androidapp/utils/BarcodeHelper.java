package com.example.androidapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Transaction;
import com.example.androidapp.data.entities.Account;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.text.SimpleDateFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * مساعد الباركود والفواتير المتقدم - إنتاج وقراءة الباركود للفواتير والمعاملات
 * Advanced Barcode and Invoice Helper - generating and reading barcodes for invoices and transactions
 */
public class BarcodeHelper {
    
    private static final String TAG = "BarcodeHelper";
    
    // إعدادات الباركود
    private static final int BARCODE_WIDTH = 512;
    private static final int BARCODE_HEIGHT = 512;
    private static final int INVOICE_BARCODE_WIDTH = 300;
    private static final int INVOICE_BARCODE_HEIGHT = 300;
    
    // أنواع البيانات
    public static final String TYPE_INVOICE = "INVOICE";
    public static final String TYPE_TRANSACTION = "TRANSACTION";
    public static final String TYPE_ACCOUNT = "ACCOUNT";
    public static final String TYPE_PAYMENT = "PAYMENT";
    
    // تنسيقات التصدير
    public static final String FORMAT_JSON = "JSON";
    public static final String FORMAT_CSV = "CSV";
    public static final String FORMAT_XML = "XML";
    
    private Context context;
    private AppDatabase database;
    private ExecutorService executorService;
    private Gson gson;
    
    public BarcodeHelper(Context context) {
        this.context = context;
        this.database = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
        this.executorService = Executors.newCachedThreadPool();
        this.gson = new Gson();
    }
    
    /**
     * إنتاج باركود للفاتورة
     * Generate barcode for invoice
     */
    public Future<BarcodeResult> generateInvoiceBarcode(InvoiceData invoiceData) {
        return executorService.submit(() -> {
            try {
                // إنشاء البيانات المشفرة للفاتورة
                InvoiceBarcodeData barcodeData = new InvoiceBarcodeData();
                barcodeData.type = TYPE_INVOICE;
                barcodeData.invoiceId = invoiceData.invoiceId;
                barcodeData.amount = invoiceData.totalAmount;
                barcodeData.date = invoiceData.date;
                barcodeData.customerInfo = invoiceData.customerInfo;
                barcodeData.items = invoiceData.items;
                barcodeData.currency = invoiceData.currency;
                barcodeData.companyInfo = invoiceData.companyInfo;
                barcodeData.timestamp = System.currentTimeMillis();
                
                // تحويل إلى JSON
                String jsonData = gson.toJson(barcodeData);
                
                // ضغط البيانات إذا كانت كبيرة
                if (jsonData.length() > 2000) {
                    jsonData = compressInvoiceData(barcodeData);
                }
                
                // إنتاج الباركود
                Bitmap barcodeBitmap = generateQRCode(jsonData, INVOICE_BARCODE_WIDTH, INVOICE_BARCODE_HEIGHT);
                
                if (barcodeBitmap != null) {
                    // حفظ الباركود
                    File barcodeFile = saveBarcodeToFile(barcodeBitmap, "invoice_" + invoiceData.invoiceId);
                    
                    return new BarcodeResult(true, "تم إنتاج باركود الفاتورة بنجاح", 
                            barcodeFile.getAbsolutePath(), jsonData, barcodeBitmap);
                } else {
                    return new BarcodeResult(false, "فشل في إنتاج باركود الفاتورة");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating invoice barcode", e);
                return new BarcodeResult(false, "خطأ في إنتاج باركود الفاتورة: " + e.getMessage());
            }
        });
    }
    
    /**
     * إنتاج باركود للمعاملة
     * Generate barcode for transaction
     */
    public Future<BarcodeResult> generateTransactionBarcode(long transactionId) {
        return executorService.submit(() -> {
            try {
                // جلب بيانات المعاملة
                Transaction transaction = database.transactionDao().getTransactionByIdSync(transactionId);
                if (transaction == null) {
                    return new BarcodeResult(false, "المعاملة غير موجودة");
                }
                
                // إنشاء بيانات الباركود
                TransactionBarcodeData barcodeData = new TransactionBarcodeData();
                barcodeData.type = TYPE_TRANSACTION;
                barcodeData.transactionId = transaction.id;
                barcodeData.amount = transaction.amount;
                barcodeData.date = transaction.date;
                barcodeData.description = transaction.description;
                barcodeData.fromAccountId = transaction.fromAccountId;
                barcodeData.toAccountId = transaction.toAccountId;
                barcodeData.categoryId = transaction.categoryId;
                barcodeData.timestamp = System.currentTimeMillis();
                
                // تحويل إلى JSON
                String jsonData = gson.toJson(barcodeData);
                
                // إنتاج الباركود
                Bitmap barcodeBitmap = generateQRCode(jsonData, BARCODE_WIDTH, BARCODE_HEIGHT);
                
                if (barcodeBitmap != null) {
                    File barcodeFile = saveBarcodeToFile(barcodeBitmap, "transaction_" + transactionId);
                    
                    return new BarcodeResult(true, "تم إنتاج باركود المعاملة بنجاح", 
                            barcodeFile.getAbsolutePath(), jsonData, barcodeBitmap);
                } else {
                    return new BarcodeResult(false, "فشل في إنتاج باركود المعاملة");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating transaction barcode", e);
                return new BarcodeResult(false, "خطأ في إنتاج باركود المعاملة: " + e.getMessage());
            }
        });
    }
    
    /**
     * إنتاج باركود لمعلومات الدفع
     * Generate barcode for payment info
     */
    public Future<BarcodeResult> generatePaymentBarcode(PaymentInfo paymentInfo) {
        return executorService.submit(() -> {
            try {
                PaymentBarcodeData barcodeData = new PaymentBarcodeData();
                barcodeData.type = TYPE_PAYMENT;
                barcodeData.paymentMethod = paymentInfo.paymentMethod;
                barcodeData.amount = paymentInfo.amount;
                barcodeData.currency = paymentInfo.currency;
                barcodeData.merchantInfo = paymentInfo.merchantInfo;
                barcodeData.bankInfo = paymentInfo.bankInfo;
                barcodeData.reference = paymentInfo.reference;
                barcodeData.timestamp = System.currentTimeMillis();
                
                String jsonData = gson.toJson(barcodeData);
                Bitmap barcodeBitmap = generateQRCode(jsonData, BARCODE_WIDTH, BARCODE_HEIGHT);
                
                if (barcodeBitmap != null) {
                    File barcodeFile = saveBarcodeToFile(barcodeBitmap, "payment_" + System.currentTimeMillis());
                    
                    return new BarcodeResult(true, "تم إنتاج باركود الدفع بنجاح", 
                            barcodeFile.getAbsolutePath(), jsonData, barcodeBitmap);
                } else {
                    return new BarcodeResult(false, "فشل في إنتاج باركود الدفع");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating payment barcode", e);
                return new BarcodeResult(false, "خطأ في إنتاج باركود الدفع: " + e.getMessage());
            }
        });
    }
    
    /**
     * قراءة وتحليل بيانات الباركود
     * Read and parse barcode data
     */
    public Future<BarcodeParseResult> parseBarcodeData(String barcodeData) {
        return executorService.submit(() -> {
            try {
                // محاولة تحليل البيانات كـ JSON
                Map<String, Object> dataMap = gson.fromJson(barcodeData, Map.class);
                String type = (String) dataMap.get("type");
                
                if (type == null) {
                    return new BarcodeParseResult(false, "نوع البيانات غير محدد");
                }
                
                switch (type) {
                    case TYPE_INVOICE:
                        InvoiceBarcodeData invoiceData = gson.fromJson(barcodeData, InvoiceBarcodeData.class);
                        return new BarcodeParseResult(true, "تم تحليل بيانات الفاتورة بنجاح", type, invoiceData);
                        
                    case TYPE_TRANSACTION:
                        TransactionBarcodeData transactionData = gson.fromJson(barcodeData, TransactionBarcodeData.class);
                        return new BarcodeParseResult(true, "تم تحليل بيانات المعاملة بنجاح", type, transactionData);
                        
                    case TYPE_PAYMENT:
                        PaymentBarcodeData paymentData = gson.fromJson(barcodeData, PaymentBarcodeData.class);
                        return new BarcodeParseResult(true, "تم تحليل بيانات الدفع بنجاح", type, paymentData);
                        
                    default:
                        return new BarcodeParseResult(false, "نوع البيانات غير مدعوم: " + type);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error parsing barcode data", e);
                return new BarcodeParseResult(false, "خطأ في تحليل بيانات الباركود: " + e.getMessage());
            }
        });
    }
    
    /**
     * استيراد الفاتورة من الباركود
     * Import invoice from barcode
     */
    public Future<ImportResult> importInvoiceFromBarcode(String barcodeData, String userId) {
        return executorService.submit(() -> {
            try {
                BarcodeParseResult parseResult = parseBarcodeData(barcodeData).get();
                
                if (!parseResult.isSuccess() || !TYPE_INVOICE.equals(parseResult.getDataType())) {
                    return new ImportResult(false, "بيانات الباركود غير صحيحة أو ليست فاتورة");
                }
                
                InvoiceBarcodeData invoiceData = (InvoiceBarcodeData) parseResult.getParsedData();
                
                // إنشاء المعاملات من الفاتورة
                List<Transaction> transactions = createTransactionsFromInvoice(invoiceData, userId);
                
                // حفظ المعاملات
                for (Transaction transaction : transactions) {
                    database.transactionDao().insert(transaction);
                }
                
                // تحديث أرصدة الحسابات
                updateAccountBalances(transactions);
                
                return new ImportResult(true, "تم استيراد الفاتورة بنجاح", transactions.size());
                
            } catch (Exception e) {
                Log.e(TAG, "Error importing invoice from barcode", e);
                return new ImportResult(false, "خطأ في استيراد الفاتورة: " + e.getMessage());
            }
        });
    }
    
    /**
     * تصدير البيانات إلى باركود
     * Export data to barcode
     */
    public Future<ExportResult> exportDataToBarcode(List<Long> transactionIds, String format) {
        return executorService.submit(() -> {
            try {
                // جمع البيانات
                List<Transaction> transactions = new ArrayList<>();
                for (Long id : transactionIds) {
                    Transaction transaction = database.transactionDao().getTransactionByIdSync(id);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
                
                if (transactions.isEmpty()) {
                    return new ExportResult(false, "لا توجد معاملات للتصدير");
                }
                
                // تنسيق البيانات
                String exportData = formatDataForExport(transactions, format);
                
                // إنتاج الباركود
                Bitmap barcodeBitmap = generateQRCode(exportData, BARCODE_WIDTH, BARCODE_HEIGHT);
                
                if (barcodeBitmap != null) {
                    File barcodeFile = saveBarcodeToFile(barcodeBitmap, "export_" + System.currentTimeMillis());
                    
                    return new ExportResult(true, "تم تصدير البيانات بنجاح", 
                            barcodeFile.getAbsolutePath(), transactions.size(), barcodeBitmap);
                } else {
                    return new ExportResult(false, "فشل في إنتاج باركود التصدير");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error exporting data to barcode", e);
                return new ExportResult(false, "خطأ في تصدير البيانات: " + e.getMessage());
            }
        });
    }
    
    /**
     * إنتاج فاتورة كاملة مع باركود
     * Generate complete invoice with barcode
     */
    public Future<InvoiceGenerationResult> generateCompleteInvoice(InvoiceData invoiceData) {
        return executorService.submit(() -> {
            try {
                // إنتاج باركود الفاتورة
                BarcodeResult barcodeResult = generateInvoiceBarcode(invoiceData).get();
                
                if (!barcodeResult.isSuccess()) {
                    return new InvoiceGenerationResult(false, "فشل في إنتاج باركود الفاتورة");
                }
                
                // إنتاج تصميم الفاتورة
                Bitmap invoiceBitmap = generateInvoiceDesign(invoiceData, barcodeResult.getBitmap());
                
                // حفظ الفاتورة
                File invoiceFile = saveInvoiceToFile(invoiceBitmap, "invoice_" + invoiceData.invoiceId);
                
                return new InvoiceGenerationResult(true, "تم إنتاج الفاتورة بنجاح", 
                        invoiceFile.getAbsolutePath(), barcodeResult.getFilePath(), invoiceBitmap);
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating complete invoice", e);
                return new InvoiceGenerationResult(false, "خطأ في إنتاج الفاتورة: " + e.getMessage());
            }
        });
    }
    
    /**
     * إنتاج QR Code
     * Generate QR Code
     */
    private Bitmap generateQRCode(String data, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            return bitmap;
            
        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code", e);
            return null;
        }
    }
    
    /**
     * حفظ الباركود في ملف
     * Save barcode to file
     */
    private File saveBarcodeToFile(Bitmap bitmap, String fileName) throws IOException {
        File barcodeDir = new File(context.getExternalFilesDir(null), "barcodes");
        if (!barcodeDir.exists()) {
            barcodeDir.mkdirs();
        }
        
        File barcodeFile = new File(barcodeDir, fileName + ".png");
        
        try (FileOutputStream fos = new FileOutputStream(barcodeFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        
        return barcodeFile;
    }
    
    /**
     * حفظ الفاتورة في ملف
     * Save invoice to file
     */
    private File saveInvoiceToFile(Bitmap bitmap, String fileName) throws IOException {
        File invoiceDir = new File(context.getExternalFilesDir(null), "invoices");
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }
        
        File invoiceFile = new File(invoiceDir, fileName + ".png");
        
        try (FileOutputStream fos = new FileOutputStream(invoiceFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);
        }
        
        return invoiceFile;
    }
    
    /**
     * ضغط بيانات الفاتورة
     * Compress invoice data
     */
    private String compressInvoiceData(InvoiceBarcodeData data) {
        // إنشاء نسخة مضغوطة من البيانات تحتوي على المعلومات الأساسية فقط
        InvoiceBarcodeData compressed = new InvoiceBarcodeData();
        compressed.type = data.type;
        compressed.invoiceId = data.invoiceId;
        compressed.amount = data.amount;
        compressed.date = data.date;
        compressed.customerInfo = data.customerInfo;
        compressed.currency = data.currency;
        compressed.timestamp = data.timestamp;
        
        // تقليل تفاصيل العناصر
        compressed.items = new ArrayList<>();
        for (InvoiceItem item : data.items) {
            InvoiceItem compressedItem = new InvoiceItem();
            compressedItem.name = item.name;
            compressedItem.quantity = item.quantity;
            compressedItem.price = item.price;
            compressed.items.add(compressedItem);
        }
        
        return gson.toJson(compressed);
    }
    
    /**
     * إنشاء معاملات من الفاتورة
     * Create transactions from invoice
     */
    private List<Transaction> createTransactionsFromInvoice(InvoiceBarcodeData invoiceData, String userId) {
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            // إنشاء معاملة رئيسية للفاتورة
            Transaction mainTransaction = new Transaction();
            mainTransaction.amount = invoiceData.amount;
            mainTransaction.date = invoiceData.date;
            mainTransaction.description = "فاتورة رقم: " + invoiceData.invoiceId;
            mainTransaction.userId = userId;
            mainTransaction.type = "INVOICE";
            mainTransaction.lastModified = System.currentTimeMillis();
            
            transactions.add(mainTransaction);
            
            // يمكن إضافة معاملات فرعية للعناصر إذا لزم الأمر
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating transactions from invoice", e);
        }
        
        return transactions;
    }
    
    /**
     * تحديث أرصدة الحسابات
     * Update account balances
     */
    private void updateAccountBalances(List<Transaction> transactions) {
        try {
            for (Transaction transaction : transactions) {
                if (transaction.fromAccountId > 0) {
                    Account fromAccount = database.accountDao().getAccountByIdSync(transaction.fromAccountId);
                    if (fromAccount != null) {
                        fromAccount.balance -= Math.abs(transaction.amount);
                        database.accountDao().update(fromAccount);
                    }
                }
                
                if (transaction.toAccountId > 0) {
                    Account toAccount = database.accountDao().getAccountByIdSync(transaction.toAccountId);
                    if (toAccount != null) {
                        toAccount.balance += Math.abs(transaction.amount);
                        database.accountDao().update(toAccount);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating account balances", e);
        }
    }
    
    /**
     * تنسيق البيانات للتصدير
     * Format data for export
     */
    private String formatDataForExport(List<Transaction> transactions, String format) {
        switch (format) {
            case FORMAT_JSON:
                return gson.toJson(transactions);
                
            case FORMAT_CSV:
                return formatTransactionsAsCSV(transactions);
                
            case FORMAT_XML:
                return formatTransactionsAsXML(transactions);
                
            default:
                return gson.toJson(transactions);
        }
    }
    
    /**
     * تنسيق المعاملات كـ CSV
     * Format transactions as CSV
     */
    private String formatTransactionsAsCSV(List<Transaction> transactions) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Amount,Date,Description,FromAccount,ToAccount\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        for (Transaction transaction : transactions) {
            csv.append(transaction.id).append(",")
               .append(transaction.amount).append(",")
               .append(sdf.format(new Date(transaction.date))).append(",")
               .append("\"").append(transaction.description).append("\"").append(",")
               .append(transaction.fromAccountId).append(",")
               .append(transaction.toAccountId).append("\n");
        }
        
        return csv.toString();
    }
    
    /**
     * تنسيق المعاملات كـ XML
     * Format transactions as XML
     */
    private String formatTransactionsAsXML(List<Transaction> transactions) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<transactions>\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        for (Transaction transaction : transactions) {
            xml.append("  <transaction>\n")
               .append("    <id>").append(transaction.id).append("</id>\n")
               .append("    <amount>").append(transaction.amount).append("</amount>\n")
               .append("    <date>").append(sdf.format(new Date(transaction.date))).append("</date>\n")
               .append("    <description>").append(escapeXml(transaction.description)).append("</description>\n")
               .append("    <fromAccount>").append(transaction.fromAccountId).append("</fromAccount>\n")
               .append("    <toAccount>").append(transaction.toAccountId).append("</toAccount>\n")
               .append("  </transaction>\n");
        }
        
        xml.append("</transactions>");
        return xml.toString();
    }
    
    /**
     * إنتاج تصميم الفاتورة
     * Generate invoice design
     */
    private Bitmap generateInvoiceDesign(InvoiceData invoiceData, Bitmap barcodeBitmap) {
        // إنشاء تصميم بسيط للفاتورة مع الباركود
        int width = 800;
        int height = 1200;
        
        Bitmap invoiceBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(invoiceBitmap);
        
        // خلفية بيضاء
        canvas.drawColor(Color.WHITE);
        
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        Paint normalPaint = new Paint();
        normalPaint.setColor(Color.BLACK);
        normalPaint.setTextSize(18);
        
        // عنوان الفاتورة
        canvas.drawText("فاتورة رقم: " + invoiceData.invoiceId, 50, 80, textPaint);
        
        // تاريخ الفاتورة
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        canvas.drawText("التاريخ: " + sdf.format(new Date(invoiceData.date)), 50, 120, normalPaint);
        
        // معلومات العميل
        if (invoiceData.customerInfo != null) {
            canvas.drawText("العميل: " + invoiceData.customerInfo, 50, 160, normalPaint);
        }
        
        // العناصر
        int yPosition = 220;
        canvas.drawText("العناصر:", 50, yPosition, textPaint);
        yPosition += 40;
        
        double total = 0;
        for (InvoiceItem item : invoiceData.items) {
            String itemText = item.name + " × " + item.quantity + " = " + (item.price * item.quantity);
            canvas.drawText(itemText, 70, yPosition, normalPaint);
            total += item.price * item.quantity;
            yPosition += 30;
        }
        
        // المجموع
        yPosition += 20;
        canvas.drawText("المجموع: " + total + " " + invoiceData.currency, 50, yPosition, textPaint);
        
        // إضافة الباركود
        if (barcodeBitmap != null) {
            int barcodeX = width - barcodeBitmap.getWidth() - 50;
            int barcodeY = height - barcodeBitmap.getHeight() - 100;
            canvas.drawBitmap(barcodeBitmap, barcodeX, barcodeY, null);
            
            // نص تحت الباركود
            canvas.drawText("امسح للتحقق", barcodeX + 50, barcodeY + barcodeBitmap.getHeight() + 30, normalPaint);
        }
        
        return invoiceBitmap;
    }
    
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
    
    /**
     * تنظيف الموارد
     * Cleanup resources
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // Classes مساعدة
    
    public static class BarcodeResult {
        private boolean success;
        private String message;
        private String filePath;
        private String data;
        private Bitmap bitmap;
        
        public BarcodeResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public BarcodeResult(boolean success, String message, String filePath, String data, Bitmap bitmap) {
            this.success = success;
            this.message = message;
            this.filePath = filePath;
            this.data = data;
            this.bitmap = bitmap;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getFilePath() { return filePath; }
        public String getData() { return data; }
        public Bitmap getBitmap() { return bitmap; }
    }
    
    public static class BarcodeParseResult {
        private boolean success;
        private String message;
        private String dataType;
        private Object parsedData;
        
        public BarcodeParseResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public BarcodeParseResult(boolean success, String message, String dataType, Object parsedData) {
            this.success = success;
            this.message = message;
            this.dataType = dataType;
            this.parsedData = parsedData;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getDataType() { return dataType; }
        public Object getParsedData() { return parsedData; }
    }
    
    public static class ImportResult {
        private boolean success;
        private String message;
        private int itemsImported;
        
        public ImportResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ImportResult(boolean success, String message, int itemsImported) {
            this.success = success;
            this.message = message;
            this.itemsImported = itemsImported;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getItemsImported() { return itemsImported; }
    }
    
    public static class ExportResult {
        private boolean success;
        private String message;
        private String filePath;
        private int itemsExported;
        private Bitmap bitmap;
        
        public ExportResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ExportResult(boolean success, String message, String filePath, int itemsExported, Bitmap bitmap) {
            this.success = success;
            this.message = message;
            this.filePath = filePath;
            this.itemsExported = itemsExported;
            this.bitmap = bitmap;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getFilePath() { return filePath; }
        public int getItemsExported() { return itemsExported; }
        public Bitmap getBitmap() { return bitmap; }
    }
    
    public static class InvoiceGenerationResult {
        private boolean success;
        private String message;
        private String invoiceFilePath;
        private String barcodeFilePath;
        private Bitmap bitmap;
        
        public InvoiceGenerationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public InvoiceGenerationResult(boolean success, String message, String invoiceFilePath, String barcodeFilePath, Bitmap bitmap) {
            this.success = success;
            this.message = message;
            this.invoiceFilePath = invoiceFilePath;
            this.barcodeFilePath = barcodeFilePath;
            this.bitmap = bitmap;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getInvoiceFilePath() { return invoiceFilePath; }
        public String getBarcodeFilePath() { return barcodeFilePath; }
        public Bitmap getBitmap() { return bitmap; }
    }
    
    // Data classes
    
    public static class InvoiceData {
        public String invoiceId;
        public long date;
        public String customerInfo;
        public String companyInfo;
        public List<InvoiceItem> items;
        public double totalAmount;
        public String currency;
    }
    
    public static class InvoiceItem {
        public String name;
        public int quantity;
        public double price;
    }
    
    public static class PaymentInfo {
        public String paymentMethod;
        public double amount;
        public String currency;
        public String merchantInfo;
        public String bankInfo;
        public String reference;
    }
    
    public static class InvoiceBarcodeData {
        public String type;
        public String invoiceId;
        public double amount;
        public long date;
        public String customerInfo;
        public String companyInfo;
        public List<InvoiceItem> items;
        public String currency;
        public long timestamp;
    }
    
    public static class TransactionBarcodeData {
        public String type;
        public long transactionId;
        public double amount;
        public long date;
        public String description;
        public long fromAccountId;
        public long toAccountId;
        public long categoryId;
        public long timestamp;
    }
    
    public static class PaymentBarcodeData {
        public String type;
        public String paymentMethod;
        public double amount;
        public String currency;
        public String merchantInfo;
        public String bankInfo;
        public String reference;
        public long timestamp;
    }
}
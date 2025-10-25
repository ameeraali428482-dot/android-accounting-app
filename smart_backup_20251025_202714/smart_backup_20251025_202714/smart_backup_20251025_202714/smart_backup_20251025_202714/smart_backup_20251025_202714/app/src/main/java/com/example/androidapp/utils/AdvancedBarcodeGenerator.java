package com.example.androidapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * مولد الباركود المتقدم - لإنشاء وإدارة رموز QR والباركود
 */
public class AdvancedBarcodeGenerator {

    private static final String TAG = "AdvancedBarcodeGenerator";
    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 512;
    
    private Context context;
    
    public AdvancedBarcodeGenerator(Context context) {
        this.context = context;
    }

    /**
     * إنشاء QR Code لفاتورة مع بيانات شاملة
     */
    public Bitmap generateInvoiceQRCode(String invoiceId, String customerName, 
                                        double totalAmount, String currency,
                                        String dueDate, String businessInfo) {
        try {
            JSONObject invoiceData = new JSONObject();
            invoiceData.put("type", "INVOICE");
            invoiceData.put("invoice_id", invoiceId);
            invoiceData.put("customer_name", customerName);
            invoiceData.put("total_amount", totalAmount);
            invoiceData.put("currency", currency);
            invoiceData.put("due_date", dueDate);
            invoiceData.put("business_info", businessInfo);
            invoiceData.put("generated_at", System.currentTimeMillis());
            
            return generateQRCode(invoiceData.toString(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creating invoice QR data", e);
            return null;
        }
    }

    /**
     * إنشاء QR Code لمعلومات العميل
     */
    public Bitmap generateCustomerQRCode(String customerId, String customerName,
                                        String phone, String email, String address) {
        try {
            JSONObject customerData = new JSONObject();
            customerData.put("type", "CUSTOMER");
            customerData.put("customer_id", customerId);
            customerData.put("name", customerName);
            customerData.put("phone", phone);
            customerData.put("email", email);
            customerData.put("address", address);
            customerData.put("generated_at", System.currentTimeMillis());
            
            return generateQRCode(customerData.toString(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creating customer QR data", e);
            return null;
        }
    }

    /**
     * إنشاء QR Code لمعلومات الصنف
     */
    public Bitmap generateItemQRCode(String itemId, String itemName, String description,
                                     double price, String category, String unit) {
        try {
            JSONObject itemData = new JSONObject();
            itemData.put("type", "ITEM");
            itemData.put("item_id", itemId);
            itemData.put("name", itemName);
            itemData.put("description", description);
            itemData.put("price", price);
            itemData.put("category", category);
            itemData.put("unit", unit);
            itemData.put("generated_at", System.currentTimeMillis());
            
            return generateQRCode(itemData.toString(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creating item QR data", e);
            return null;
        }
    }

    /**
     * إنشاء QR Code لرابط مشاركة التطبيق
     */
    public Bitmap generateAppShareQRCode(String referralCode, String appDownloadUrl,
                                        String businessName, String incentiveText) {
        try {
            JSONObject shareData = new JSONObject();
            shareData.put("type", "APP_SHARE");
            shareData.put("referral_code", referralCode);
            shareData.put("download_url", appDownloadUrl);
            shareData.put("business_name", businessName);
            shareData.put("incentive", incentiveText);
            shareData.put("generated_at", System.currentTimeMillis());
            
            return generateQRCode(shareData.toString(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creating app share QR data", e);
            return null;
        }
    }

    /**
     * إنشاء QR Code عام مع إعدادات مخصصة
     */
    public Bitmap generateQRCode(String content, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            
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
     * حفظ QR Code في ملف
     */
    public String saveQRCodeToFile(Bitmap qrBitmap, String fileName) {
        try {
            File qrDir = new File(context.getFilesDir(), "qrcodes");
            if (!qrDir.exists()) {
                qrDir.mkdirs();
            }
            
            File qrFile = new File(qrDir, fileName + ".png");
            
            FileOutputStream out = new FileOutputStream(qrFile);
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            
            return qrFile.getAbsolutePath();
            
        } catch (IOException e) {
            Log.e(TAG, "Error saving QR code to file", e);
            return null;
        }
    }

    /**
     * تحويل Bitmap إلى byte array
     */
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * إنشاء QR Code مع لوجو في الوسط
     */
    public Bitmap generateQRCodeWithLogo(String content, Bitmap logo, int width, int height) {
        try {
            Bitmap qrBitmap = generateQRCode(content, width, height);
            if (qrBitmap == null || logo == null) {
                return qrBitmap;
            }
            
            // حساب حجم اللوجو (حوالي 10% من حجم QR)
            int logoSize = width / 10;
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, false);
            
            // دمج اللوجو في وسط QR Code
            Bitmap result = qrBitmap.copy(Bitmap.Config.ARGB_8888, true);
            android.graphics.Canvas canvas = new android.graphics.Canvas(result);
            
            int logoX = (width - logoSize) / 2;
            int logoY = (height - logoSize) / 2;
            
            // رسم خلفية بيضاء للوجو
            android.graphics.Paint whitePaint = new android.graphics.Paint();
            whitePaint.setColor(Color.WHITE);
            canvas.drawRect(logoX - 5, logoY - 5, logoX + logoSize + 5, logoY + logoSize + 5, whitePaint);
            
            // رسم اللوجو
            canvas.drawBitmap(scaledLogo, logoX, logoY, null);
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating QR code with logo", e);
            return generateQRCode(content, width, height);
        }
    }

    /**
     * إنشاء باركود مع معلومات مشفرة
     */
    public Bitmap generateEncryptedQRCode(String content, String encryptionKey,
                                         int width, int height) {
        try {
            // تشفير بسيط (يمكن تحسينه لاحقاً)
            String encryptedContent = simpleEncrypt(content, encryptionKey);
            
            JSONObject encryptedData = new JSONObject();
            encryptedData.put("encrypted", true);
            encryptedData.put("data", encryptedContent);
            encryptedData.put("timestamp", System.currentTimeMillis());
            
            return generateQRCode(encryptedData.toString(), width, height);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creating encrypted QR code", e);
            return null;
        }
    }

    /**
     * تشفير بسيط (يجب استبداله بتشفير قوي في الإنتاج)
     */
    private String simpleEncrypt(String content, String key) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            char keyChar = key.charAt(keyIndex % key.length());
            result.append((char) (c ^ keyChar));
            keyIndex++;
        }
        
        return android.util.Base64.encodeToString(
            result.toString().getBytes(), 
            android.util.Base64.DEFAULT
        );
    }

    /**
     * فك تشفير بسيط
     */
    public String simpleDecrypt(String encryptedContent, String key) {
        try {
            byte[] decodedBytes = android.util.Base64.decode(encryptedContent, android.util.Base64.DEFAULT);
            String decoded = new String(decodedBytes);
            
            StringBuilder result = new StringBuilder();
            int keyIndex = 0;
            
            for (int i = 0; i < decoded.length(); i++) {
                char c = decoded.charAt(i);
                char keyChar = key.charAt(keyIndex % key.length());
                result.append((char) (c ^ keyChar));
                keyIndex++;
            }
            
            return result.toString();
            
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting content", e);
            return null;
        }
    }
}

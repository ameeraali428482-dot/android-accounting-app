package com.example.androidapp.ui.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;





public class BarcodeScannerActivity extends AppCompatActivity {

    public static final String EXTRA_BARCODE_RESULT = "barcode_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No layout needed for this activity, it will launch the scanner directly
        startScanner();
    }

    private void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("امسح الباركود أو رمز الاستجابة السريعة");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "تم إلغاء المسح", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "تم مسح: " + result.getContents(), Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_BARCODE_RESULT, result.getContents());
                setResult(RESULT_OK, returnIntent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finish(); // Finish this activity after scan result
    }
}

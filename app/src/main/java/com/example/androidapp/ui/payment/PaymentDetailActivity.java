package com.example.androidapp.ui.payment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class PaymentDetailActivity extends AppCompatActivity {
    private EditText editPaymentDate, editAmount, editReferenceNumber, editNotes;
    private AutoCompleteTextView editPayerId;
    private Spinner spinnerPayerType, spinnerPaymentMethod;
    private Button buttonSave, buttonCancel;
    private PaymentDao paymentDao;
    private String companyId;
    private String paymentId;
    private Payment currentPayment;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        initViews();
        setupSpinners();
        setupDatePicker();

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCurrentCompanyId();
        AppDatabase db = AppDatabase.getDatabase(this);
        paymentDao = db.paymentDao();

        paymentId = getIntent().getStringExtra("payment_id");
        if (paymentId != null) {
            loadPayment();
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editPaymentDate.setText(dateFormat.format(calendar.getTime()));
        }

        buttonSave.setOnClickListener(v -> savePayment());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        editPaymentDate = findViewById(R.id.editPaymentDate);
        editAmount = findViewById(R.id.editAmount);
        editReferenceNumber = findViewById(R.id.editReferenceNumber);
        editNotes = findViewById(R.id.editNotes);
        editPayerId = findViewById(R.id.editPayerId);
        spinnerPayerType = findViewById(R.id.spinnerPayerType);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private void setupSpinners() {
        String[] payerTypes = {"عميل", "مورد"};
        ArrayAdapter<String> payerTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, payerTypes);
        payerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayerType.setAdapter(payerTypeAdapter);

        String[] paymentMethods = {"نقد", "شيك", "تحويل بنكي", "بطاقة ائتمان", "أخرى"};
        ArrayAdapter<String> paymentMethodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(paymentMethodAdapter);
    }

    private void setupDatePicker() {
        editPaymentDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PaymentDetailActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        editPaymentDate.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void loadPayment() {
        paymentDao.getPaymentById(paymentId, companyId).observe(this, payment -> {
            if (payment != null) {
                currentPayment = payment;
                populateFields();
            } else {
                Toast.makeText(this, "لم يتم العثور على المدفوعة", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields() {
        editPaymentDate.setText(currentPayment.getPaymentDate());
        editPayerId.setText(currentPayment.getPayerId());
        editAmount.setText(String.valueOf(currentPayment.getAmount()));
        editReferenceNumber.setText(currentPayment.getReferenceNumber());
        editNotes.setText(currentPayment.getNotes());

        if ("Customer".equals(currentPayment.getPayerType())) {
            spinnerPayerType.setSelection(0);
        } else {
            spinnerPayerType.setSelection(1);
        }

        String[] paymentMethods = {"نقد", "شيك", "تحويل بنكي", "بطاقة ائتمان", "أخرى"};
        for (int i = 0; i < paymentMethods.length; i++) {
            if (paymentMethods[i].equals(currentPayment.getPaymentMethod())) {
                spinnerPaymentMethod.setSelection(i);
                break;
            }
        }
    }

    private void savePayment() {
        if (!validateInput()) {
            return;
        }

        String paymentDate = editPaymentDate.getText().toString().trim();
        String payerId = editPayerId.getText().toString().trim();
        String payerType = spinnerPayerType.getSelectedItem().toString().equals("عميل") ? "Customer" : "Supplier";
        float amount = Float.parseFloat(editAmount.getText().toString().trim());
        String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
        String referenceNumber = editReferenceNumber.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            if (currentPayment == null) {
                String newPaymentId = UUID.randomUUID().toString();
                Payment newPayment = new Payment(newPaymentId, companyId, paymentDate, payerId, payerType, amount, paymentMethod, referenceNumber, notes, "COMPLETED");
                paymentDao.insert(newPayment);
            } else {
                currentPayment.setPaymentDate(paymentDate);
                currentPayment.setPayerId(payerId);
                currentPayment.setPayerType(payerType);
                currentPayment.setAmount(amount);
                currentPayment.setPaymentMethod(paymentMethod);
                currentPayment.setReferenceNumber(referenceNumber);
                currentPayment.setNotes(notes);
                paymentDao.update(currentPayment);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ المدفوعة بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private boolean validateInput() {
        if (editPaymentDate.getText().toString().trim().isEmpty()) {
            editPaymentDate.setError("يرجى إدخال تاريخ المدفوعة");
            return false;
        }
        if (editPayerId.getText().toString().trim().isEmpty()) {
            editPayerId.setError("يرجى إدخال معرف الدافع");
            return false;
        }
        if (editAmount.getText().toString().trim().isEmpty()) {
            editAmount.setError("يرجى إدخال المبلغ");
            return false;
        }
        try {
            float amount = Float.parseFloat(editAmount.getText().toString().trim());
            if (amount <= 0) {
                editAmount.setError("يجب أن يكون المبلغ أكبر من صفر");
                return false;
            }
        } catch (NumberFormatException e) {
            editAmount.setError("يرجى إدخال مبلغ صحيح");
            return false;
        }
        return true;
    }
}

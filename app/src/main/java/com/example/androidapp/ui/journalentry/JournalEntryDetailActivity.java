package com.example.androidapp.ui.journalentry;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.data.entities.JournalEntryItem;
import com.example.androidapp.ui.journalentry.viewmodel.JournalEntryViewModel;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;






public class JournalEntryDetailActivity extends AppCompatActivity {

    private EditText etDate, etDescription, etReferenceNumber, etEntryType, etTotalDebit, etTotalCredit;
    private Button btnSave, btnDelete;
    private JournalEntryViewModel viewModel;
    private SessionManager sessionManager;
    private String companyId;
    private String journalEntryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry_detail);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(JournalEntryViewModel.class);

        initViews();
        setupListeners();

        journalEntryId = getIntent().getStringExtra("journal_entry_id");

        if (journalEntryId != null) {
            setTitle("تعديل قيد يومية");
            loadJournalEntryDetails(journalEntryId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("إضافة قيد يومية جديد");
            etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            btnDelete.setVisibility(View.GONE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveJournalEntry());
        btnDelete.setOnClickListener(v -> deleteJournalEntry());
    }

    private void loadJournalEntryDetails(String id) {
        viewModel.getJournalEntryById(id, companyId).observe(this, journalEntry -> {
            if (journalEntry != null) {
                etDate.setText(journalEntry.getEntryDate());
                etDescription.setText(journalEntry.getDescription());
                etReferenceNumber.setText(journalEntry.getReferenceNumber());
                etEntryType.setText(journalEntry.getEntryType());
                etTotalDebit.setText(String.valueOf(journalEntry.getTotalDebit()));
                etTotalCredit.setText(String.valueOf(journalEntry.getTotalCredit()));
            } else {
                Toast.makeText(this, "لم يتم العثور على قيد اليومية", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveJournalEntry() {
        String date = etDate.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String referenceNumber = etReferenceNumber.getText().toString().trim();
        String entryType = etEntryType.getText().toString().trim();
        float totalDebit = Float.parseFloat(etTotalDebit.getText().toString().trim().isEmpty() ? "0" : etTotalDebit.getText().toString().trim());
        float totalCredit = Float.parseFloat(etTotalCredit.getText().toString().trim().isEmpty() ? "0" : etTotalCredit.getText().toString().trim());

        if (date.isEmpty() || description.isEmpty() || referenceNumber.isEmpty() || entryType.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول الرئيسية.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Math.abs(totalDebit - totalCredit) > 0.01) { // Allow for small floating point inaccuracies
            Toast.makeText(this, "يجب أن يتساوى إجمالي المدين والدائن.", Toast.LENGTH_LONG).show();
            return;
        }

        JournalEntry journalEntry;
        if (journalEntryId == null) {
            // New entry
            journalEntryId = UUID.randomUUID().toString();
            journalEntry = new JournalEntry(journalEntryId, companyId, date, description, referenceNumber, entryType, totalDebit, totalCredit);
            viewModel.insert(journalEntry);
            Toast.makeText(this, "تم إضافة قيد اليومية بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing entry
            journalEntry = new JournalEntry(journalEntryId, companyId, date, description, referenceNumber, entryType, totalDebit, totalCredit);
            viewModel.update(journalEntry);
            Toast.makeText(this, "تم تحديث قيد اليومية بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteJournalEntry() {
        if (journalEntryId != null) {
            viewModel.getJournalEntryById(journalEntryId, companyId).observe(this, journalEntry -> {
                if (journalEntry != null) {
                    viewModel.delete(journalEntry);
                    Toast.makeText(this, "تم حذف قيد اليومية بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

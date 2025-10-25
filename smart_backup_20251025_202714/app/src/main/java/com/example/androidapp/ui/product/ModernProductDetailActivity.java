package com.example.androidapp.ui.product;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.models.Product;
import com.example.androidapp.models.ProductVariant;
import com.example.androidapp.models.Supplier;
import com.example.androidapp.ui.barcode.BarcodeGeneratorActivity;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * نشاط تفاصيل المنتج المتطور - Material 3 Design
 * مع جميع الميزات الاحترافية لإدارة المنتجات
 */
public class ModernProductDetailActivity extends AppCompatActivity {

    // UI Components - Header
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ShapeableImageView productImageView;
    private TextView productNameText;
    private TextView productCodeText;
    private TextView stockStatusText;
    private CircularProgressIndicator stockProgressIndicator;

    // Product Information
    private TextInputEditText productNameEditText;
    private TextInputEditText productCodeEditText;
    private VoiceInputEditText productDescriptionEditText;
    private MaterialAutoCompleteTextView categoryDropdown;
    private MaterialAutoCompleteTextView supplierDropdown;

    // Pricing Section
    private TextInputEditText costPriceEditText;
    private TextInputEditText sellingPriceEditText;
    private TextInputEditText discountPercentEditText;
    private TextView finalPriceText;
    private TextView profitMarginText;

    // Stock Management
    private TextInputEditText currentStockEditText;
    private TextInputEditText minStockLevelEditText;
    private TextInputEditText maxStockLevelEditText;
    private TextInputEditText reorderPointEditText;

    // Product Properties
    private ChipGroup statusChipGroup;
    private Chip activeChip;
    private Chip inactiveChip;
    private Chip discontinuedChip;

    private ChipGroup typeChipGroup;
    private Chip productChip;
    private Chip serviceChip;
    private Chip digitalChip;

    // Barcode Section
    private MaterialCardView barcodeCard;
    private TextView barcodeText;
    private MaterialButton generateBarcodeButton;
    private MaterialButton scanBarcodeButton;

    // Statistics Cards
    private MaterialCardView salesStatsCard;
    private MaterialCardView stockStatsCard;
    private MaterialCardView profitStatsCard;
    
    private TextView totalSalesText;
    private TextView salesQuantityText;
    private TextView stockValueText;
    private TextView stockDaysText;
    private TextView totalProfitText;
    private TextView profitPercentText;

    // Tabs and Content
    private TabLayout tabLayout;
    private RecyclerView variantsRecyclerView;
    private RecyclerView historyRecyclerView;
    private View suppliersView;

    // Action Buttons
    private MaterialButton saveButton;
    private MaterialButton duplicateButton;
    private MaterialButton shareButton;
    private MaterialButton deleteButton;

    // FAB
    private FloatingActionButton addVariantFab;

    // Image Handling
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    // Data and Adapters
    private Product product;
    private List<ProductVariant> productVariants;
    private List<Supplier> suppliers;
    private ProductVariantAdapter variantAdapter;

    // Managers
    private VoiceInputManager voiceInputManager;
    private SmartSuggestionsManager suggestionsManager;
    private Material3Helper material3Helper;

    // Formatters
    private NumberFormat currencyFormatter;
    private NumberFormat percentFormatter;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_modern);

        // Initialize managers
        voiceInputManager = VoiceInputManager.getInstance(this);
        suggestionsManager = SmartSuggestionsManager.getInstance(this);
        material3Helper = Material3Helper.getInstance();

        // Initialize formatters
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        percentFormatter = NumberFormat.getPercentInstance(new Locale("ar", "SA"));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get product from intent
        getProductFromIntent();

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Apply Material 3 styling
        material3Helper.applyMaterial3Styling(this);

        // Setup listeners
        setupListeners();

        // Setup tabs
        setupTabs();

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup dropdowns
        setupDropdowns();

        // Setup voice input
        setupVoiceInput();

        // Setup image handling
        setupImageHandling();

        // Load product data
        loadProductData();

        // Load related data
        loadProductVariants();
        loadSuppliers();

        // Update calculations
        updatePriceCalculations();
        updateStatistics();
    }

    private void getProductFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("product")) {
            product = (Product) intent.getSerializableExtra("product");
        } else {
            // Create new product
            product = new Product();
        }
    }

    private void initializeViews() {
        // Header
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        productImageView = findViewById(R.id.productImageView);
        productNameText = findViewById(R.id.productNameText);
        productCodeText = findViewById(R.id.productCodeText);
        stockStatusText = findViewById(R.id.stockStatusText);
        stockProgressIndicator = findViewById(R.id.stockProgressIndicator);

        // Product Information
        productNameEditText = findViewById(R.id.productNameEditText);
        productCodeEditText = findViewById(R.id.productCodeEditText);
        productDescriptionEditText = findViewById(R.id.productDescriptionEditText);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        supplierDropdown = findViewById(R.id.supplierDropdown);

        // Pricing
        costPriceEditText = findViewById(R.id.costPriceEditText);
        sellingPriceEditText = findViewById(R.id.sellingPriceEditText);
        discountPercentEditText = findViewById(R.id.discountPercentEditText);
        finalPriceText = findViewById(R.id.finalPriceText);
        profitMarginText = findViewById(R.id.profitMarginText);

        // Stock Management
        currentStockEditText = findViewById(R.id.currentStockEditText);
        minStockLevelEditText = findViewById(R.id.minStockLevelEditText);
        maxStockLevelEditText = findViewById(R.id.maxStockLevelEditText);
        reorderPointEditText = findViewById(R.id.reorderPointEditText);

        // Status Chips
        statusChipGroup = findViewById(R.id.statusChipGroup);
        activeChip = findViewById(R.id.activeChip);
        inactiveChip = findViewById(R.id.inactiveChip);
        discontinuedChip = findViewById(R.id.discontinuedChip);

        // Type Chips
        typeChipGroup = findViewById(R.id.typeChipGroup);
        productChip = findViewById(R.id.productChip);
        serviceChip = findViewById(R.id.serviceChip);
        digitalChip = findViewById(R.id.digitalChip);

        // Barcode
        barcodeCard = findViewById(R.id.barcodeCard);
        barcodeText = findViewById(R.id.barcodeText);
        generateBarcodeButton = findViewById(R.id.generateBarcodeButton);
        scanBarcodeButton = findViewById(R.id.scanBarcodeButton);

        // Statistics
        salesStatsCard = findViewById(R.id.salesStatsCard);
        stockStatsCard = findViewById(R.id.stockStatsCard);
        profitStatsCard = findViewById(R.id.profitStatsCard);
        
        totalSalesText = findViewById(R.id.totalSalesText);
        salesQuantityText = findViewById(R.id.salesQuantityText);
        stockValueText = findViewById(R.id.stockValueText);
        stockDaysText = findViewById(R.id.stockDaysText);
        totalProfitText = findViewById(R.id.totalProfitText);
        profitPercentText = findViewById(R.id.profitPercentText);

        // Tabs
        tabLayout = findViewById(R.id.tabLayout);
        variantsRecyclerView = findViewById(R.id.variantsRecyclerView);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        suppliersView = findViewById(R.id.suppliersView);

        // Action Buttons
        saveButton = findViewById(R.id.saveButton);
        duplicateButton = findViewById(R.id.duplicateButton);
        shareButton = findViewById(R.id.shareButton);
        deleteButton = findViewById(R.id.deleteButton);

        // FAB
        addVariantFab = findViewById(R.id.addVariantFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        collapsingToolbar.setTitle("تفاصيل المنتج");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white, getTheme()));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    private void setupListeners() {
        // Image click
        productImageView.setOnClickListener(v -> showImagePickerDialog());

        // Price calculation listeners
        costPriceEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) updatePriceCalculations();
        });
        sellingPriceEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) updatePriceCalculations();
        });
        discountPercentEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) updatePriceCalculations();
        });

        // Action buttons
        saveButton.setOnClickListener(v -> saveProduct());
        duplicateButton.setOnClickListener(v -> duplicateProduct());
        shareButton.setOnClickListener(v -> shareProduct());
        deleteButton.setOnClickListener(v -> deleteProduct());

        // Barcode buttons
        generateBarcodeButton.setOnClickListener(v -> generateBarcode());
        scanBarcodeButton.setOnClickListener(v -> scanBarcode());

        // FAB
        addVariantFab.setOnClickListener(v -> addProductVariant());

        // Statistics cards
        salesStatsCard.setOnClickListener(v -> showSalesDetails());
        stockStatsCard.setOnClickListener(v -> showStockMovement());
        profitStatsCard.setOnClickListener(v -> showProfitAnalysis());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("المتغيرات"));
        tabLayout.addTab(tabLayout.newTab().setText("التاريخ"));
        tabLayout.addTab(tabLayout.newTab().setText("الموردين"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTabContent(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerViews() {
        // Variants RecyclerView
        productVariants = new ArrayList<>();
        variantAdapter = new ProductVariantAdapter(this, productVariants);
        variantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        variantsRecyclerView.setAdapter(variantAdapter);
    }

    private void setupDropdowns() {
        // Categories
        String[] categories = {
            "إلكترونيات", "ملابس", "طعام ومشروبات", "كتب", 
            "أدوات منزلية", "رياضة", "سيارات", "أخرى"
        };
        categoryDropdown.setSimpleItems(categories);

        // Suppliers will be loaded from database
    }

    private void setupVoiceInput() {
        // Setup voice input for description
        voiceInputManager.setupVoiceInput(productDescriptionEditText, this);
        
        // Setup smart suggestions
        List<String> descriptionSuggestions = new ArrayList<>();
        descriptionSuggestions.add("منتج عالي الجودة");
        descriptionSuggestions.add("يتميز بالمتانة والجودة");
        descriptionSuggestions.add("مناسب لجميع الأعمار");
        suggestionsManager.setupSuggestions(productDescriptionEditText, descriptionSuggestions);
    }

    private void setupImageHandling() {
        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    productImageView.setImageURI(imageUri);
                    // TODO: Save image path to product
                }
            }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        productImageView.setImageBitmap(imageBitmap);
                        // TODO: Save image to product
                    }
                }
            }
        );
    }

    private void loadProductData() {
        if (product != null) {
            productNameText.setText(product.getName());
            productCodeText.setText("كود المنتج: " + product.getCode());
            
            // Fill form fields
            productNameEditText.setText(product.getName());
            productCodeEditText.setText(product.getCode());
            productDescriptionEditText.setText(product.getDescription());
            categoryDropdown.setText(product.getCategory(), false);
            
            costPriceEditText.setText(String.valueOf(product.getCostPrice()));
            sellingPriceEditText.setText(String.valueOf(product.getSellingPrice()));
            discountPercentEditText.setText(String.valueOf(product.getDiscountPercent()));
            
            currentStockEditText.setText(String.valueOf(product.getCurrentStock()));
            minStockLevelEditText.setText(String.valueOf(product.getMinStockLevel()));
            maxStockLevelEditText.setText(String.valueOf(product.getMaxStockLevel()));
            reorderPointEditText.setText(String.valueOf(product.getReorderPoint()));
            
            barcodeText.setText(product.getBarcode());
            
            // Set status chips
            updateStatusChips();
            updateStockStatus();
        }
    }

    private void loadProductVariants() {
        // TODO: Load variants from database
        productVariants.clear();
        variantAdapter.notifyDataSetChanged();
    }

    private void loadSuppliers() {
        // TODO: Load suppliers from database
        suppliers = new ArrayList<>();
        
        // Update supplier dropdown
        String[] supplierNames = suppliers.stream()
            .map(Supplier::getName)
            .toArray(String[]::new);
        supplierDropdown.setSimpleItems(supplierNames);
    }

    private void updateStatusChips() {
        switch (product.getStatus()) {
            case ACTIVE:
                activeChip.setChecked(true);
                break;
            case INACTIVE:
                inactiveChip.setChecked(true);
                break;
            case DISCONTINUED:
                discontinuedChip.setChecked(true);
                break;
        }

        switch (product.getType()) {
            case PRODUCT:
                productChip.setChecked(true);
                break;
            case SERVICE:
                serviceChip.setChecked(true);
                break;
            case DIGITAL:
                digitalChip.setChecked(true);
                break;
        }
    }

    private void updateStockStatus() {
        int currentStock = product.getCurrentStock();
        int minLevel = product.getMinStockLevel();
        int maxLevel = product.getMaxStockLevel();

        if (currentStock <= 0) {
            stockStatusText.setText("نفد المخزون");
            stockStatusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, getTheme()));
            stockProgressIndicator.setProgress(0);
        } else if (currentStock <= minLevel) {
            stockStatusText.setText("مخزون منخفض");
            stockStatusText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, getTheme()));
            stockProgressIndicator.setProgress(25);
        } else if (currentStock >= maxLevel) {
            stockStatusText.setText("مخزون عالي");
            stockStatusText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark, getTheme()));
            stockProgressIndicator.setProgress(100);
        } else {
            stockStatusText.setText("مخزون جيد");
            stockStatusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
            int progress = (int) ((double) currentStock / maxLevel * 100);
            stockProgressIndicator.setProgress(progress);
        }
    }

    private void updatePriceCalculations() {
        try {
            double costPrice = Double.parseDouble(costPriceEditText.getText().toString());
            double sellingPrice = Double.parseDouble(sellingPriceEditText.getText().toString());
            double discountPercent = Double.parseDouble(discountPercentEditText.getText().toString());

            double discountAmount = sellingPrice * (discountPercent / 100);
            double finalPrice = sellingPrice - discountAmount;
            double profitAmount = finalPrice - costPrice;
            double profitMargin = (profitAmount / costPrice) * 100;

            finalPriceText.setText(currencyFormatter.format(finalPrice));
            profitMarginText.setText(String.format("%.1f%%", profitMargin));

            // Update color based on profit
            if (profitMargin > 20) {
                profitMarginText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
            } else if (profitMargin > 10) {
                profitMarginText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, getTheme()));
            } else {
                profitMarginText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, getTheme()));
            }

        } catch (NumberFormatException e) {
            finalPriceText.setText("0.00 ر.س");
            profitMarginText.setText("0.0%");
        }
    }

    private void updateStatistics() {
        // TODO: Calculate from database
        // For now, use dummy data
        totalSalesText.setText("15,000 ر.س");
        salesQuantityText.setText("50 قطعة");
        stockValueText.setText(currencyFormatter.format(product.getCurrentStock() * product.getCostPrice()));
        stockDaysText.setText("30 يوم");
        totalProfitText.setText("3,000 ر.س");
        profitPercentText.setText("20%");
    }

    private void switchTabContent(int position) {
        // Hide all content views
        variantsRecyclerView.setVisibility(View.GONE);
        historyRecyclerView.setVisibility(View.GONE);
        suppliersView.setVisibility(View.GONE);

        // Show selected content
        switch (position) {
            case 0: // Variants
                variantsRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 1: // History
                historyRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2: // Suppliers
                suppliersView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر صورة المنتج");
        builder.setItems(new String[]{"الكاميرا", "المعرض"}, (dialog, which) -> {
            if (which == 0) {
                // Camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            } else {
                // Gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(galleryIntent);
            }
        });
        builder.show();
    }

    private void saveProduct() {
        // Validate input
        if (productNameEditText.getText().toString().trim().isEmpty()) {
            productNameEditText.setError("يرجى إدخال اسم المنتج");
            return;
        }

        // Update product object
        product.setName(productNameEditText.getText().toString().trim());
        product.setCode(productCodeEditText.getText().toString().trim());
        product.setDescription(productDescriptionEditText.getText().toString().trim());
        product.setCategory(categoryDropdown.getText().toString().trim());

        try {
            product.setCostPrice(Double.parseDouble(costPriceEditText.getText().toString()));
            product.setSellingPrice(Double.parseDouble(sellingPriceEditText.getText().toString()));
            product.setDiscountPercent(Double.parseDouble(discountPercentEditText.getText().toString()));
            
            product.setCurrentStock(Integer.parseInt(currentStockEditText.getText().toString()));
            product.setMinStockLevel(Integer.parseInt(minStockLevelEditText.getText().toString()));
            product.setMaxStockLevel(Integer.parseInt(maxStockLevelEditText.getText().toString()));
            product.setReorderPoint(Integer.parseInt(reorderPointEditText.getText().toString()));
        } catch (NumberFormatException e) {
            showSnackbar("يرجى التأكد من صحة الأرقام المدخلة", null, null);
            return;
        }

        // Update status based on chips
        if (activeChip.isChecked()) {
            product.setStatus(Product.ProductStatus.ACTIVE);
        } else if (inactiveChip.isChecked()) {
            product.setStatus(Product.ProductStatus.INACTIVE);
        } else if (discontinuedChip.isChecked()) {
            product.setStatus(Product.ProductStatus.DISCONTINUED);
        }

        if (productChip.isChecked()) {
            product.setType(Product.ProductType.PRODUCT);
        } else if (serviceChip.isChecked()) {
            product.setType(Product.ProductType.SERVICE);
        } else if (digitalChip.isChecked()) {
            product.setType(Product.ProductType.DIGITAL);
        }

        // TODO: Save to database
        
        showSnackbar("تم حفظ بيانات المنتج بنجاح", "موافق", null);
        
        // Update UI
        loadProductData();
        updatePriceCalculations();
        updateStatistics();
    }

    private void duplicateProduct() {
        showSnackbar("سيتم نسخ المنتج", null, null);
    }

    private void shareProduct() {
        String shareText = "معلومات المنتج:\n" +
                "الاسم: " + product.getName() + "\n" +
                "الكود: " + product.getCode() + "\n" +
                "السعر: " + currencyFormatter.format(product.getSellingPrice()) + "\n" +
                "المخزون: " + product.getCurrentStock();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "مشاركة معلومات المنتج"));
    }

    private void deleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("حذف المنتج");
        builder.setMessage("هل تريد حقاً حذف هذا المنتج؟");
        builder.setPositiveButton("نعم", (dialog, which) -> {
            // TODO: Delete from database
            showSnackbar("تم حذف المنتج", "تراجع", null);
            finish();
        });
        builder.setNegativeButton("لا", null);
        builder.show();
    }

    private void generateBarcode() {
        Intent intent = new Intent(this, BarcodeGeneratorActivity.class);
        intent.putExtra("product_code", product.getCode());
        startActivity(intent);
    }

    private void scanBarcode() {
        // TODO: Implement barcode scanning
        showSnackbar("سيتم فتح الماسح الضوئي", null, null);
    }

    private void addProductVariant() {
        showSnackbar("سيتم إضافة متغير جديد", null, null);
    }

    private void showSalesDetails() {
        showSnackbar("عرض تفاصيل المبيعات", null, null);
    }

    private void showStockMovement() {
        showSnackbar("عرض حركة المخزون", null, null);
    }

    private void showProfitAnalysis() {
        showSnackbar("عرض تحليل الأرباح", null, null);
    }

    private void showSnackbar(String message, String actionText, View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        if (actionText != null && actionListener != null) {
            snackbar.setAction(actionText, actionListener);
        }
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
                
            case R.id.action_barcode:
                generateBarcode();
                return true;
                
            case R.id.action_qr_code:
                showSnackbar("سيتم إنشاء QR Code", null, null);
                return true;
                
            case R.id.action_export:
                showSnackbar("سيتم تصدير بيانات المنتج", null, null);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
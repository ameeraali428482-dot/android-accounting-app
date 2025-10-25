package com.example.androidapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.OrderItem;
import com.example.androidapp.ui.order.adapters.OrderItemAdapter;
import com.example.androidapp.ui.order.viewmodels.OrderDetailViewModel;
import com.example.androidapp.ui.product.ProductDetailActivity;
import com.example.androidapp.utils.PermissionManager;
import com.example.androidapp.utils.DateTimeUtils;
import com.example.androidapp.utils.CurrencyUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    
    private static final String TAG = "OrderDetailActivity";
    public static final String EXTRA_ORDER_ID = "order_id";
    
    // UI Components
    private Toolbar toolbar;
    private MaterialCardView orderInfoCard;
    private TextView orderNumberText;
    private TextView orderDateText;
    private TextView orderStatusText;
    private TextView customerNameText;
    private TextView customerPhoneText;
    private TextView orderTotalText;
    private TextView orderNotesText;
    private ImageView orderStatusIcon;
    
    private RecyclerView orderItemsRecyclerView;
    private OrderItemAdapter orderItemAdapter;
    
    private MaterialCardView deliveryInfoCard;
    private TextView deliveryAddressText;
    private TextView deliveryDateText;
    private TextView deliveryNotesText;
    
    private Button editOrderButton;
    private Button cancelOrderButton;
    private Button completeOrderButton;
    private FloatingActionButton fabEditOrder;
    
    // Data
    private OrderDetailViewModel viewModel;
    private PermissionManager permissionManager;
    private String orderId;
    private Order currentOrder;
    private List<OrderItem> orderItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        
        // Initialize components
        initializeViews();
        initializeData();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        
        // Load order data
        loadOrderData();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        orderInfoCard = findViewById(R.id.orderInfoCard);
        orderNumberText = findViewById(R.id.orderNumberText);
        orderDateText = findViewById(R.id.orderDateText);
        orderStatusText = findViewById(R.id.orderStatusText);
        customerNameText = findViewById(R.id.customerNameText);
        customerPhoneText = findViewById(R.id.customerPhoneText);
        orderTotalText = findViewById(R.id.orderTotalText);
        orderNotesText = findViewById(R.id.orderNotesText);
        orderStatusIcon = findViewById(R.id.orderStatusIcon);
        
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        
        deliveryInfoCard = findViewById(R.id.deliveryInfoCard);
        deliveryAddressText = findViewById(R.id.deliveryAddressText);
        deliveryDateText = findViewById(R.id.deliveryDateText);
        deliveryNotesText = findViewById(R.id.deliveryNotesText);
        
        editOrderButton = findViewById(R.id.editOrderButton);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        completeOrderButton = findViewById(R.id.completeOrderButton);
        fabEditOrder = findViewById(R.id.fabEditOrder);
    }
    
    private void initializeData() {
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);
        permissionManager = new PermissionManager(this);
        
        // Get order ID from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ORDER_ID)) {
            orderId = intent.getStringExtra(EXTRA_ORDER_ID);
        }
        
        if (orderId == null || orderId.isEmpty()) {
            Log.e(TAG, "No order ID provided");
            Toast.makeText(this, "خطأ في تحميل بيانات الطلب", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        orderItems = new ArrayList<>();
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("تفاصيل الطلب");
        }
    }
    
    private void setupRecyclerView() {
        orderItemAdapter = new OrderItemAdapter(orderItems, this::onOrderItemClick);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemsRecyclerView.setAdapter(orderItemAdapter);
    }
    
    private void setupClickListeners() {
        editOrderButton.setOnClickListener(v -> editOrder());
        cancelOrderButton.setOnClickListener(v -> showCancelOrderDialog());
        completeOrderButton.setOnClickListener(v -> completeOrder());
        fabEditOrder.setOnClickListener(v -> editOrder());
        
        customerPhoneText.setOnClickListener(v -> {
            if (currentOrder != null && currentOrder.getCustomerPhone() != null) {
                // Call customer phone
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(android.net.Uri.parse("tel:" + currentOrder.getCustomerPhone()));
                startActivity(callIntent);
            }
        });
    }
    
    private void loadOrderData() {
        viewModel.getOrder(orderId).observe(this, order -> {
            if (order != null) {
                currentOrder = order;
                displayOrderData(order);
                updateButtonStates();
            } else {
                Toast.makeText(this, "لم يتم العثور على الطلب", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        viewModel.getOrderItems(orderId).observe(this, items -> {
            if (items != null) {
                orderItems.clear();
                orderItems.addAll(items);
                orderItemAdapter.notifyDataSetChanged();
            }
        });
    }
    
    private void displayOrderData(Order order) {
        // Basic order information
        orderNumberText.setText("رقم الطلب: " + order.getOrderNumber());
        orderDateText.setText("تاريخ الطلب: " + DateTimeUtils.formatDate(order.getCreatedAt()));
        orderStatusText.setText(getStatusText(order.getStatus()));
        customerNameText.setText("العميل: " + order.getCustomerName());
        customerPhoneText.setText("الهاتف: " + order.getCustomerPhone());
        orderTotalText.setText("المجموع: " + CurrencyUtils.formatAmount(order.getTotalAmount()));
        
        // Order notes
        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            orderNotesText.setText(order.getNotes());
            orderNotesText.setVisibility(View.VISIBLE);
        } else {
            orderNotesText.setVisibility(View.GONE);
        }
        
        // Status icon and color
        updateStatusDisplay(order.getStatus());
        
        // Delivery information
        if (order.getDeliveryAddress() != null && !order.getDeliveryAddress().isEmpty()) {
            deliveryAddressText.setText("العنوان: " + order.getDeliveryAddress());
            deliveryInfoCard.setVisibility(View.VISIBLE);
        } else {
            deliveryInfoCard.setVisibility(View.GONE);
        }
        
        if (order.getDeliveryDate() != null) {
            deliveryDateText.setText("تاريخ التسليم: " + DateTimeUtils.formatDate(order.getDeliveryDate()));
        }
        
        if (order.getDeliveryNotes() != null && !order.getDeliveryNotes().isEmpty()) {
            deliveryNotesText.setText("ملاحظات التسليم: " + order.getDeliveryNotes());
            deliveryNotesText.setVisibility(View.VISIBLE);
        } else {
            deliveryNotesText.setVisibility(View.GONE);
        }
    }
    
    private void updateStatusDisplay(String status) {
        int colorResId;
        int iconResId;
        
        switch (status.toLowerCase()) {
            case "pending":
                colorResId = android.R.color.holo_orange_dark;
                iconResId = android.R.drawable.ic_menu_recent_history;
                break;
            case "confirmed":
                colorResId = android.R.color.holo_blue_dark;
                iconResId = android.R.drawable.ic_menu_agenda;
                break;
            case "processing":
                colorResId = android.R.color.holo_purple;
                iconResId = android.R.drawable.ic_menu_preferences;
                break;
            case "shipped":
                colorResId = android.R.color.holo_blue_light;
                iconResId = android.R.drawable.ic_menu_send;
                break;
            case "delivered":
                colorResId = android.R.color.holo_green_dark;
                iconResId = android.R.drawable.ic_menu_compass;
                break;
            case "cancelled":
                colorResId = android.R.color.holo_red_dark;
                iconResId = android.R.drawable.ic_menu_close_clear_cancel;
                break;
            default:
                colorResId = android.R.color.darker_gray;
                iconResId = android.R.drawable.ic_menu_help;
                break;
        }
        
        orderStatusText.setTextColor(getResources().getColor(colorResId, getTheme()));
        orderStatusIcon.setImageResource(iconResId);
        orderStatusIcon.setColorFilter(getResources().getColor(colorResId, getTheme()));
    }
    
    private String getStatusText(String status) {
        switch (status.toLowerCase()) {
            case "pending": return "قيد الانتظار";
            case "confirmed": return "مؤكد";
            case "processing": return "قيد التحضير";
            case "shipped": return "تم الشحن";
            case "delivered": return "تم التسليم";
            case "cancelled": return "ملغي";
            default: return "غير معروف";
        }
    }
    
    private void updateButtonStates() {
        if (currentOrder == null) return;
        
        String status = currentOrder.getStatus().toLowerCase();
        
        // Check permissions
        permissionManager.hasPermission(PermissionManager.PERM_EDIT_TRANSACTIONS).thenAccept(canEdit -> {
            runOnUiThread(() -> {
                boolean canEditOrder = canEdit && !status.equals("delivered") && !status.equals("cancelled");
                boolean canCancelOrder = canEdit && !status.equals("delivered") && !status.equals("cancelled");
                boolean canCompleteOrder = canEdit && (status.equals("confirmed") || status.equals("processing") || status.equals("shipped"));
                
                editOrderButton.setEnabled(canEditOrder);
                fabEditOrder.setVisibility(canEditOrder ? View.VISIBLE : View.GONE);
                cancelOrderButton.setEnabled(canCancelOrder);
                completeOrderButton.setEnabled(canCompleteOrder);
                
                // Update button text based on status
                if (status.equals("pending")) {
                    completeOrderButton.setText("تأكيد الطلب");
                } else if (status.equals("confirmed")) {
                    completeOrderButton.setText("بدء التحضير");
                } else if (status.equals("processing")) {
                    completeOrderButton.setText("شحن الطلب");
                } else if (status.equals("shipped")) {
                    completeOrderButton.setText("تأكيد التسليم");
                }
            });
        });
    }
    
    private void editOrder() {
        Intent intent = new Intent(this, OrderEditActivity.class);
        intent.putExtra("order_id", orderId);
        startActivityForResult(intent, 100);
    }
    
    private void showCancelOrderDialog() {
        new AlertDialog.Builder(this)
                .setTitle("إلغاء الطلب")
                .setMessage("هل أنت متأكد من إلغاء هذا الطلب؟")
                .setPositiveButton("إلغاء الطلب", (dialog, which) -> cancelOrder())
                .setNegativeButton("تراجع", null)
                .show();
    }
    
    private void cancelOrder() {
        if (currentOrder == null) return;
        
        viewModel.updateOrderStatus(orderId, "cancelled").observe(this, success -> {
            if (success) {
                Toast.makeText(this, "تم إلغاء الطلب بنجاح", Toast.LENGTH_SHORT).show();
                loadOrderData(); // Refresh data
            } else {
                Toast.makeText(this, "فشل في إلغاء الطلب", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void completeOrder() {
        if (currentOrder == null) return;
        
        String currentStatus = currentOrder.getStatus().toLowerCase();
        String nextStatus;
        
        switch (currentStatus) {
            case "pending":
                nextStatus = "confirmed";
                break;
            case "confirmed":
                nextStatus = "processing";
                break;
            case "processing":
                nextStatus = "shipped";
                break;
            case "shipped":
                nextStatus = "delivered";
                break;
            default:
                Toast.makeText(this, "لا يمكن تحديث حالة الطلب", Toast.LENGTH_SHORT).show();
                return;
        }
        
        viewModel.updateOrderStatus(orderId, nextStatus).observe(this, success -> {
            if (success) {
                Toast.makeText(this, "تم تحديث حالة الطلب بنجاح", Toast.LENGTH_SHORT).show();
                loadOrderData(); // Refresh data
            } else {
                Toast.makeText(this, "فشل في تحديث حالة الطلب", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void onOrderItemClick(OrderItem item) {
        // Handle order item click (e.g., show product details)
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", item.getProductId());
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_share) {
            shareOrder();
            return true;
        } else if (id == R.id.action_print) {
            printOrder();
            return true;
        } else if (id == R.id.action_duplicate) {
            duplicateOrder();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void shareOrder() {
        if (currentOrder == null) return;
        
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("تفاصيل الطلب\n");
        orderDetails.append("==============\n");
        orderDetails.append("رقم الطلب: ").append(currentOrder.getOrderNumber()).append("\n");
        orderDetails.append("التاريخ: ").append(DateTimeUtils.formatDate(currentOrder.getCreatedAt())).append("\n");
        orderDetails.append("العميل: ").append(currentOrder.getCustomerName()).append("\n");
        orderDetails.append("الحالة: ").append(getStatusText(currentOrder.getStatus())).append("\n");
        orderDetails.append("المجموع: ").append(CurrencyUtils.formatAmount(currentOrder.getTotalAmount())).append("\n");
        
        if (currentOrder.getNotes() != null && !currentOrder.getNotes().isEmpty()) {
            orderDetails.append("الملاحظات: ").append(currentOrder.getNotes()).append("\n");
        }
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, orderDetails.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "تفاصيل الطلب رقم " + currentOrder.getOrderNumber());
        
        startActivity(Intent.createChooser(shareIntent, "مشاركة تفاصيل الطلب"));
    }
    
    private void printOrder() {
        // TODO: Implement print functionality
        Toast.makeText(this, "ميزة الطباعة قيد التطوير", Toast.LENGTH_SHORT).show();
    }
    
    private void duplicateOrder() {
        if (currentOrder == null) return;
        
        Intent intent = new Intent(this, OrderCreateActivity.class);
        intent.putExtra("duplicate_order_id", orderId);
        startActivity(intent);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Order was edited, refresh data
            loadOrderData();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadOrderData();
    }
}
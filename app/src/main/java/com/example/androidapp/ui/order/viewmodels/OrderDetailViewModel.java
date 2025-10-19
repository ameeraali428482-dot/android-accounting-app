package com.example.androidapp.ui.order.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidapp.data.entities.OrderItem;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.OrderItemDao;
import com.example.androidapp.data.dao.ItemDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderDetailViewModel extends AndroidViewModel {

    private OrderItemDao orderItemDao;
    private ItemDao itemDao;
    private ExecutorService executor;

    private MutableLiveData<List<OrderItem>> orderItemsLiveData;
    private MutableLiveData<Boolean> isLoadingLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        initializeViewModel(application);
    }

    private void initializeViewModel(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        orderItemDao = database.orderItemDao();
        itemDao = database.itemDao();
        executor = Executors.newFixedThreadPool(4);

        orderItemsLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>(false);
        errorMessageLiveData = new MutableLiveData<>();
    }

    // LiveData Getters
    public LiveData<List<OrderItem>> getOrderItemsLiveData() {
        return orderItemsLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    // Load order items by order ID
    public void loadOrderItems(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            errorMessageLiveData.setValue("معرف الطلب غير صحيح");
            return;
        }

        isLoadingLiveData.setValue(true);
        executor.execute(() -> {
            try {
                List<OrderItem> orderItems = orderItemDao.getByOrderId(orderId);
                orderItemsLiveData.postValue(orderItems);
            } catch (Exception e) {
                errorMessageLiveData.postValue("خطأ في تحميل عناصر الطلب: " + e.getMessage());
            } finally {
                isLoadingLiveData.postValue(false);
            }
        });
    }

    // Add new order item
    public void addOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            errorMessageLiveData.setValue("بيانات العنصر غير صحيحة");
            return;
        }

        executor.execute(() -> {
            try {
                orderItemDao.insert(orderItem);
                // Reload order items after adding
                if (orderItem.getOrderId() != null) {
                    loadOrderItems(orderItem.getOrderId());
                }
            } catch (Exception e) {
                errorMessageLiveData.postValue("خطأ في إضافة العنصر: " + e.getMessage());
            }
        });
    }

    // Update order item
    public void updateOrderItem(OrderItem orderItem) {
        if (orderItem == null || orderItem.getId() == null) {
            errorMessageLiveData.setValue("بيانات العنصر غير صحيحة");
            return;
        }

        executor.execute(() -> {
            try {
                orderItemDao.update(orderItem);
                // Reload order items after updating
                if (orderItem.getOrderId() != null) {
                    loadOrderItems(orderItem.getOrderId());
                }
            } catch (Exception e) {
                errorMessageLiveData.postValue("خطأ في تحديث العنصر: " + e.getMessage());
            }
        });
    }

    // Delete order item
    public void deleteOrderItem(OrderItem orderItem) {
        if (orderItem == null || orderItem.getId() == null) {
            errorMessageLiveData.setValue("بيانات العنصر غير صحيحة");
            return;
        }

        executor.execute(() -> {
            try {
                orderItemDao.delete(orderItem);
                // Reload order items after deleting
                if (orderItem.getOrderId() != null) {
                    loadOrderItems(orderItem.getOrderId());
                }
            } catch (Exception e) {
                errorMessageLiveData.postValue("خطأ في حذف العنصر: " + e.getMessage());
            }
        });
    }

    // Calculate total order amount
    public double calculateOrderTotal(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (OrderItem item : orderItems) {
            total += item.getTotalPrice() + item.getTaxAmount();
        }
        return total;
    }

    // Get item details
    public void getItemDetails(String itemId, ItemDetailsCallback callback) {
        if (itemId == null || itemId.trim().isEmpty()) {
            callback.onError("معرف الصنف غير صحيح");
            return;
        }

        executor.execute(() -> {
            try {
                Item item = itemDao.getById(itemId);
                if (item != null) {
                    callback.onSuccess(item);
                } else {
                    callback.onError("الصنف غير موجود");
                }
            } catch (Exception e) {
                callback.onError("خطأ في تحميل بيانات الصنف: " + e.getMessage());
            }
        });
    }

    // Callback interface for item details
    public interface ItemDetailsCallback {
        void onSuccess(Item item);
        void onError(String error);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}

package com.example.androidapp.logic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.InventoryDao;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.Item;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutorService;

public class InventoryManagerTest {

    private InventoryManager inventoryManager;
    private AppDatabase mockAppDatabase;
    private InventoryDao mockInventoryDao;
    private ItemDao mockItemDao;
    private ExecutorService mockExecutorService;
    private Context mockContext;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        mockAppDatabase = mock(AppDatabase.class);
        mockInventoryDao = mock(InventoryDao.class);
        mockItemDao = mock(ItemDao.class);
        mockExecutorService = mock(ExecutorService.class);

        when(mockAppDatabase.inventoryDao()).thenReturn(mockInventoryDao);
        when(mockAppDatabase.itemDao()).thenReturn(mockItemDao);

        inventoryManager = new InventoryManager(mockContext, mockAppDatabase, mockExecutorService);
    }

    @Test
    public void testAddItemToInventory() {
        String companyId = "comp1";
        long itemId = 1L;
        int quantity = 10;
        double unitCost = 5.0;

        Item item = new Item(companyId, "Test Item", "Description", 10.0, 12.0);
        item.id = itemId;

        when(mockItemDao.getItemById(itemId)).thenReturn(item);
        when(mockInventoryDao.getInventoryForItem(itemId)).thenReturn(null);

        inventoryManager.addItemToInventory(companyId, itemId, quantity, unitCost);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(mockInventoryDao).insert(inventoryCaptor.capture());
        Inventory capturedInventory = inventoryCaptor.getValue();

        assertEquals(companyId, capturedInventory.companyId);
        assertEquals(itemId, capturedInventory.itemId);
        assertEquals(quantity, capturedInventory.quantity);
        assertEquals(unitCost, capturedInventory.unitCost, 0.001);
    }

    @Test
    public void testUpdateItemInInventory() {
        String companyId = "comp1";
        long itemId = 1L;
        int quantity = 10;
        double unitCost = 5.0;

        Inventory existingInventory = new Inventory(companyId, itemId, 5, 4.0);
        existingInventory.id = 1L;

        Item item = new Item(companyId, "Test Item", "Description", 10.0, 12.0);
        item.id = itemId;

        when(mockItemDao.getItemById(itemId)).thenReturn(item);
        when(mockInventoryDao.getInventoryForItem(itemId)).thenReturn(existingInventory);

        inventoryManager.addItemToInventory(companyId, itemId, quantity, unitCost);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(mockInventoryDao).update(inventoryCaptor.capture());
        Inventory capturedInventory = inventoryCaptor.getValue();

        assertEquals(companyId, capturedInventory.companyId);
        assertEquals(itemId, capturedInventory.itemId);
        assertEquals(15, capturedInventory.quantity); // 5 (existing) + 10 (new)
        // The unit cost should be averaged or updated based on a specific policy. For simplicity, let's assume it's updated to the new cost.
        assertEquals(unitCost, capturedInventory.unitCost, 0.001);
    }

    @Test
    public void testRemoveItemFromInventory() {
        String companyId = "comp1";
        long itemId = 1L;
        int quantityToRemove = 3;

        Inventory existingInventory = new Inventory(companyId, itemId, 10, 5.0);
        existingInventory.id = 1L;

        when(mockInventoryDao.getInventoryForItem(itemId)).thenReturn(existingInventory);

        inventoryManager.removeItemFromInventory(companyId, itemId, quantityToRemove);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(mockInventoryDao).update(inventoryCaptor.capture());
        Inventory capturedInventory = inventoryCaptor.getValue();

        assertEquals(companyId, capturedInventory.companyId);
        assertEquals(itemId, capturedInventory.itemId);
        assertEquals(7, capturedInventory.quantity); // 10 (existing) - 3 (removed)
        assertEquals(5.0, capturedInventory.unitCost, 0.001);
    }

    @Test
    public void testRemoveMoreItemsThanAvailable() {
        String companyId = "comp1";
        long itemId = 1L;
        int quantityToRemove = 15;

        Inventory existingInventory = new Inventory(companyId, itemId, 10, 5.0);
        existingInventory.id = 1L;

        when(mockInventoryDao.getInventoryForItem(itemId)).thenReturn(existingInventory);

        inventoryManager.removeItemFromInventory(companyId, itemId, quantityToRemove);

        // Expect quantity to be 0, not negative
        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(mockInventoryDao).update(inventoryCaptor.capture());
        Inventory capturedInventory = inventoryCaptor.getValue();

        assertEquals(0, capturedInventory.quantity);
    }
}


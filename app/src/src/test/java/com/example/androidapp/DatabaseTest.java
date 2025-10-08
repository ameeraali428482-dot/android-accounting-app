package com.example.androidapp;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CompanyDao;
import com.example.androidapp.data.entities.Company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private CompanyDao companyDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        companyDao = db.companyDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeCompanyAndReadInList() throws Exception {
        Company company = new Company("1", "Test Company", "test@example.com", "123456789", "Address", "Logo", "USD", "Active", "2023-01-01", "2023-01-01");
        companyDao.insert(company);
        Company byId = companyDao.getCompanyById("1");
        assertNotNull(byId);
        assertEquals(company.getName(), byId.getName());
    }
}


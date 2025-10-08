package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

import com.example.androidapp.data.entities.CurrencyExchange;

@Dao
public interface CurrencyExchangeDao {
    @Insert
    void insert(CurrencyExchange currencyexchange);

    @Update
    void update(CurrencyExchange currencyexchange);

    @Delete
    void delete(CurrencyExchange currencyexchange);

    @Query("SELECT * FROM currencyexchanges")
    List<CurrencyExchange> getAllCurrencyExchanges();

    @Query("SELECT * FROM currencyexchanges WHERE id = :id LIMIT 1")
    CurrencyExchange getCurrencyExchangeById(String id);
}

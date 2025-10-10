package com.example.androidapp.data;

import java.util.List;



public interface IRepository<T> {
    void insert(T item);
    void update(T item);
    void delete(T item);
    T getById(String id);
    List<T> getAll();
}

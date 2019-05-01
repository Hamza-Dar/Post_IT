package com.example.project;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface postDAO {
    @Query("SELECT * FROM post")
    List<post_save> getAll();

    @Insert
    void insertAll(post_save... users);
}

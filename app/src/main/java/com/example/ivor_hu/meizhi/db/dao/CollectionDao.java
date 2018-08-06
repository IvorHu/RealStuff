package com.example.ivor_hu.meizhi.db.dao;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.ivor_hu.meizhi.db.entity.Stuff;

/**
 * Created by ivor on 2017/11/25.
 */

@Dao
public interface CollectionDao {
    @Query("SELECT * FROM collection ORDER BY lastChanged DESC")
    DataSource.Factory<Integer, Stuff> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Stuff... stuffs);

    @Delete
    void delete(Stuff... stuffs);
}

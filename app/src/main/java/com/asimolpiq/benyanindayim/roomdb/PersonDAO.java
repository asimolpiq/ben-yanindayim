package com.asimolpiq.benyanindayim.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.asimolpiq.benyanindayim.model.Person;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface PersonDAO {
    @Query("SELECT * FROM Person")
    Flowable<List<Person>> getAll();

    @Insert
    Completable insert(Person person);

    @Update
    Completable updatePerson(Person person);

    @Delete
    Completable delete(Person person);
}

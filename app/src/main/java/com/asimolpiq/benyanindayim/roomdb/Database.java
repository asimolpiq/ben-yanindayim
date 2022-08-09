package com.asimolpiq.benyanindayim.roomdb;

import androidx.room.RoomDatabase;

import com.asimolpiq.benyanindayim.model.Person;

@androidx.room.Database(entities = {Person.class},version = 1)
public abstract class Database extends RoomDatabase {
    public abstract PersonDAO personDAO();
}

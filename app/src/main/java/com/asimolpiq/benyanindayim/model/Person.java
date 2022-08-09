package com.asimolpiq.benyanindayim.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Person implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "personName")
    public String personName;

    @ColumnInfo(name = "phoneNumber")
    public long phoneNumber;

    @ColumnInfo(name = "isActive")
    public String isActive;

    public Person(String personName, long phoneNumber, String isActive) {
        this.personName = personName;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
    }
}

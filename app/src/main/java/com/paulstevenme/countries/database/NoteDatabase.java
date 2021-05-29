package com.paulstevenme.countries.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.paulstevenme.countries.database.entity.Note;

@Database(entities = {Note.class}, version = 2)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();

}

package com.paulstevenme.countries;

import android.content.Context;

import androidx.room.Room;

import com.paulstevenme.countries.database.NoteDatabase;

public class DatabaseClient {

    private static DatabaseClient mInstance;

    //our app database object
    private NoteDatabase noteDatabase;

    private DatabaseClient(Context mCtx) {

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        noteDatabase = Room.databaseBuilder(mCtx, NoteDatabase.class, "MyToDos").build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public NoteDatabase getNoteDatabase() {
        return noteDatabase;
    }
}

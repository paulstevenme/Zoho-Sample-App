package com.paulstevenme.countries.database;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.paulstevenme.countries.database.entity.Note;
import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM note")
    List<Note> getAllNotes();

    @Query("SELECT name FROM note")
    List<String> getAllItemNames();

    @Query("SELECT flag FROM note where name = :countryName")
    String getFlagURL(String countryName);



    @Query("SELECT * FROM note WHERE name LIKE :name LIMIT 1")
    Note getNoteByTitle(String name);


    @Insert
    void insert(Note note);

}

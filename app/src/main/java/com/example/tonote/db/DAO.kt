package com.example.tonote.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.tonote.model.Note


@Dao
interface DAO {



    @Insert(onConflict =OnConflictStrategy.IGNORE)
    suspend fun addNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query( "SELECT * FROM Note ORDER BY id DESC")
    fun getAllNote(): LiveData<List<Note>>



    @Query( "SELECT * FROM Note WHERE title LIKE :querry OR content LIKE :querry OR date LIKE :querry ORDER BY id DESC")
    fun searchNote(querry:String): LiveData<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)
}
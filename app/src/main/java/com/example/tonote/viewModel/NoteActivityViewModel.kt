package com.example.tonote.viewModel

import android.app.DownloadManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.tonote.db.DAO
import com.example.tonote.model.Note
import com.example.tonote.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val repository: NoteRepository): ViewModel() {

    fun saveNote(newNote: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.addNote(newNote)
    }

    fun updateNote(existingNote: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.updateNote(existingNote)
    }

    fun deleteNote(existingNote: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteNote(existingNote)
    }

    fun searchNote(query: String): LiveData<List<Note>>{
        return repository.searchNote(query)
    }

    fun getAllNotes():LiveData<List<Note>> = repository.getNote()
}
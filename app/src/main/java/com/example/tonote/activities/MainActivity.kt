package com.example.tonote.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.tonote.R

import com.example.tonote.db.NoteDatabase
import com.example.tonote.repository.NoteRepository
import com.example.tonote.viewModel.NoteActivityViewModel
import com.example.tonote.viewModel.NoteActivityViewModelFactory


class MainActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var binding:com.example.tonote.databinding.ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding= com.example.tonote.databinding.ActivityMainBinding.inflate(layoutInflater)
        try{
            setContentView(binding.root)
            val noteRepository=NoteRepository(NoteDatabase(this))
            val noteActivityViewModelFactory=NoteActivityViewModelFactory(noteRepository)
            noteActivityViewModel=ViewModelProvider(this,
            noteActivityViewModelFactory)[NoteActivityViewModel::class.java]
        }catch (e: Exception){
            Log.d("TAG","Error")
        }

    }
}
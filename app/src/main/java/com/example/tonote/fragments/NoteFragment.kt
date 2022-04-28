package com.example.tonote.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.tonote.R
import com.example.tonote.activities.MainActivity
import com.example.tonote.databinding.FragmentNoteBinding
import com.example.tonote.utils.hideKeyboard
import com.example.tonote.viewModel.NoteActivityViewModel
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteBinding: com.example.tonote.databinding.FragmentNoteBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        exitTransition=MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition=MaterialElevationScale(false).apply {
            duration=350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteBinding= FragmentNoteBinding.bind(view)
        val activity=activity as MainActivity
        val navController= Navigation.findNavController(view)
        requireView().hideKeyboard()
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
           // activity.window.statusBarColor= Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor=Color.parseColor("#9E9D9D")

        }

        noteBinding.addNoteFab.setOnClickListener {
            noteBinding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrDeleteFragment())
        }
        noteBinding.innerFab.setOnClickListener {
            noteBinding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrDeleteFragment())

        }

        recyclerViewDisplay()
        //implement search here
        noteBinding.search.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                noteBinding.noData.isVisible=false
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
               if(s.toString().isNotEmpty())
               {
                   val text=s.toString()
                   val query="%$text%"
                   if(query.isNotEmpty())
                   {
                       noteActivityViewModel.searchNote(query).observe(viewLifecycleOwner)
                       {

                       }
                   }
                   else{
                       observerDataChanges()
                   }
               }
               else
               {
                   observerDataChanges()
               }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        } )

        noteBinding.rvNote.setOnScrollChangeListener{_,scrollX,scrollY,_,oldScrollY ->

            when{

                scrollY>oldScrollY->{
                    noteBinding.fabText.isVisible=false
                }
                scrollX==scrollY->
                {
                    noteBinding.fabText.isVisible=true
                }
                else->
                {
                    noteBinding.fabText.isVisible=true
                }
            }


        }








    }

    private fun observerDataChanges() {

    }

    private fun recyclerViewDisplay() {

        when(resources.configuration.orientation)
        {
            Configuration.ORIENTATION_PORTRAIT-> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE-> setUpRecyclerView(3)
        }

    }

    private fun setUpRecyclerView(spanCount: Int) {



    }
}
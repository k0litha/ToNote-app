package com.example.tonote.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.tonote.R
import com.example.tonote.activities.MainActivity
import com.example.tonote.databinding.BottomSheetLayoutBinding
import com.example.tonote.databinding.FragmentSaveOrDeleteBinding
import com.example.tonote.model.Note
import com.example.tonote.utils.hideKeyboard
import com.example.tonote.viewModel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete),TextToSpeech.OnInitListener {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveOrDeleteBinding
    private var  note: Note?=null
    private lateinit var  md: String
    private var color=-1
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job= CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()
    private lateinit var result: String
    lateinit var bitmap: Bitmap
    private var tts: TextToSpeech? = null










    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent

            bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, Uri.parse(uriContent.toString()))
            val image = InputImage.fromBitmap(bitmap, 0)
            imageToText(image)


        } else {
            // an error occurred
            val exception = result.error
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation



        // TextToSpeech(Context: this, OnInitListener: this)
        tts = TextToSpeech(context?.applicationContext , this)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding= FragmentSaveOrDeleteBinding.bind(view)

        navController=Navigation.findNavController(view)
        val activity=activity as MainActivity

        val requestCamera=registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

            } else {

            }
        }


        contentBinding.ttsBtn.isEnabled = false
        contentBinding.ttsBtn.setOnClickListener{
            if (!tts!!.isSpeaking()) {
                speakOut()


            }else{
                if (tts != null) {
                    contentBinding.ttsBtn.setBackgroundTintList(context?.let { it1 ->
                        ContextCompat.getColorStateList(
                            it1,R.color.purple)  })
                    tts!!.stop()
                }
            }


        }

        contentBinding.OcrBtn.setOnClickListener{
            requestCamera.launch(android.Manifest.permission.CAMERA)
            requestCamera.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                })

        }



        ViewCompat.setTransitionName(
            contentBinding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        contentBinding.backBtn.setOnClickListener{
        requireView().hideKeyboard()
        saveNote()

        }



        try {
                contentBinding.etNoteContent.setOnFocusChangeListener{_,hasFocus->
                    if(hasFocus)
                    {
                        contentBinding.BottomBar.visibility=View.VISIBLE
                        contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                    }else contentBinding.BottomBar.visibility=View.GONE

                }
            }catch (e: Throwable)
            {
                Log.d("TAG",e.stackTraceToString())
            }

        contentBinding.fabColorPick.setOnClickListener{
                val bottomSheetDialog=BottomSheetDialog(
                    requireContext(),
                    R.style.BottomSheetDialogTheme
                )
                val bottomSheetView: View= layoutInflater.inflate(
                    R.layout.bottom_sheet_layout,
                    null
                )


                with(bottomSheetDialog)
                {
                    setContentView(bottomSheetView)
                    show()

                }
                val bottomSheetBinding=BottomSheetLayoutBinding.bind(bottomSheetView)
                bottomSheetBinding.apply{
                    colorPicker.apply{
                        setSelectedColor(color)
                        setOnColorSelectedListener {
                            setSelectedColor(color)
                            setOnColorSelectedListener {
                                value->
                                color=value
                                contentBinding.apply {
                                    noteContentFragmentParent.setBackgroundColor(color)
                                    toolBarFragmentNoteContent.setBackgroundColor(color)
                                    bar.setBackgroundColor(color)
                                    activity.window.statusBarColor=color
                                }
                                bottomSheetBinding.botttomSheetParent.setCardBackgroundColor(color)

                            }
                        }
                        botttomSheetParent.setCardBackgroundColor(color)
                    }
                    bottomSheetView.post{
                        bottomSheetDialog.behavior.state=BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }

        //opens with the existing note item
        setUpNote()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    saveNote()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }


 private fun imageToText(image: InputImage) {
     val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
     recognizer.process(image)
         .addOnSuccessListener {
             contentBinding.etNoteContent.append("\n")
             contentBinding.etNoteContent.append(it.text)
         }
         .addOnFailureListener { e ->
             // Task failed with an exception
             // ...
         }
 }



    private fun saveNote() {
        if(!contentBinding.etTitle.text.toString().isEmpty() || !contentBinding.etNoteContent.text.toString().isEmpty())
        {
            note=args.note
            when(note){
                null-> {
                    noteActivityViewModel.saveNote(
                        Note(
                            0,
                            contentBinding.etTitle.text.toString(),
                            contentBinding.etNoteContent.getMD(),
                            currentDate,
                            color
                        )
                    )
                    result = "Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )
                    navController.popBackStack()
                }
                else ->{
                    //upadte note
                    updateNote()
                    navController.popBackStack()

                }


            }
        }
        else
        {
            navController.popBackStack()
        }
    }



    private fun setUpNote() {
        val note=args.note
        val title=contentBinding.etTitle
        val content=contentBinding.etNoteContent
        val lastEdited= contentBinding.lastEdited

        if(note==null)
        {
            contentBinding.lastEdited.text=getString(R.string.edited_on,SimpleDateFormat.getDateInstance().format(Date()))
        }
        if (note!=null)
        {
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdited.text=getString(R.string.edited_on,note.date)
            color=note.color
            contentBinding.apply {
                job.launch {
                    delay(5)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolBarFragmentNoteContent.setBackgroundColor(color)
                BottomBar.setBackgroundColor(color)

            }
            activity?.window?.statusBarColor=note.color

        }
    }


    private fun updateNote() {
        if (note!=null)
        {md = SpannableStringBuilder(contentBinding.etNoteContent.getMD()).toString()
            noteActivityViewModel.updateNote(


                Note(
                    note!!.id,
                    contentBinding.etTitle.text.toString(),
                    md,
                    currentDate,
                    color,
                )
            )
        }
    }



    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                    Log.i("TextToSpeech", "On Start")
                    contentBinding.ttsBtn.setBackgroundTintList(context?.let { it1 ->
                        ContextCompat.getColorStateList(
                            it1,R.color.card_green)
                    })

                }

                override fun onDone(utteranceId: String) {
                    contentBinding.ttsBtn.setBackgroundTintList(context?.let { it1 ->
                        ContextCompat.getColorStateList(
                            it1,R.color.purple)
                    })
                }

                override fun onError(utteranceId: String) {
                    Log.i("TextToSpeech", "On Error")
                }
            })

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                contentBinding.ttsBtn.isEnabled = true
            }
        }
    }


    private fun speakOut() {

        tts!!.setSpeechRate(0.85f)
        tts!!.setSpeechRate(0.85f)
        tts!!.setPitch(1.10f)
        tts!!.speak(speakTextSelector(), TextToSpeech.QUEUE_FLUSH, null,"")

    }


    private fun speakTextSelector(): String {
        val titleStartSel: Int = contentBinding.etTitle.getSelectionStart()
        val titleEndSel: Int = contentBinding.etTitle.getSelectionEnd()
        val contentStartSel: Int = contentBinding.etNoteContent.getSelectionStart()
        val contentEndSel: Int = contentBinding.etNoteContent.getSelectionEnd()

        val selectedTitleText: String = contentBinding.etTitle.getText().toString().substring(titleStartSel, titleEndSel)
        val selectedContentText: String = contentBinding.etNoteContent.getText().toString().substring(contentStartSel, contentEndSel)
        val fullText = "" + contentBinding.etTitle.text.toString() + ". " + contentBinding.etNoteContent.text.toString()
        var textOut =""

        if (selectedTitleText == "" && selectedContentText == ""){textOut= fullText}
        else
        {
            if (selectedTitleText != "" && selectedContentText == ""){textOut = selectedTitleText}
            else if (selectedTitleText == "" && selectedContentText != ""){textOut = selectedContentText}
        }

        return textOut
    }


















     override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

}




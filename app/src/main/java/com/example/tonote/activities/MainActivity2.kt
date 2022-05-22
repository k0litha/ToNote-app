package com.example.tonote.activities

import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tonote.R
import com.example.tonote.db.DAO
import com.example.tonote.db.NoteDatabase
import com.example.tonote.model.Note


class MainActivity2 : AppCompatActivity() {
    var myListView: ListView? = null
    var db: NoteDatabase? = null
    var dao: DAO? = null
    var csr: Cursor? = null
    var sca: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        myListView = findViewById(R.id.myListView)
        db = NoteDatabase.invoke(context = applicationContext)
        dao = db?.getNoteDao()

        setUpOrRefreshListView()
    }

    /* As it says setup or refresh the ListView */
    private fun setUpOrRefreshListView() {
        csr = cursor
        if (sca == null) {
            sca = SimpleCursorAdapter(
                this,
                R.layout.demo_note_item,
                csr,
                arrayOf("title", "content","date","color"),
                intArrayOf(R.id.noteItemTitle, R.id.noteContentItem,R.id.noteDate),
                0
            )
            myListView?.setAdapter(sca)

        } else {
            sca!!.swapCursor(csr)
        }
    }

    private val cursor: Cursor
        private get() {
            val notes: List<Note>? = dao?.getAll()

            val mxcsr = MatrixCursor(
                arrayOf(
                    BaseColumns._ID,
                    "title",
                    "content",
                    "date",
                    "color"
                ),
                0
            )
            for (p in dao?.getAll()!!) {
                mxcsr.addRow(arrayOf<Any>(p.id, p.title, p.content, p.date, p.color))
            }
            return mxcsr
        }

    override fun onResume() {
        super.onResume()
        setUpOrRefreshListView()
    }

    /* if the activity is detsroyed then close the Cursor */
    override fun onDestroy() {
        super.onDestroy()
        csr?.close()
    }
}
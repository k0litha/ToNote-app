package com.example.tonote.activities

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.tonote.R
import com.example.tonote.db.DAO
import com.example.tonote.db.NoteDatabase
import com.example.tonote.model.Note

private const val REMOTE_VIEW_COUNT: Int = 10
class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListRemoteViewsFactory(this.applicationContext, intent)
    }
}

class ListRemoteViewsFactory(

    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    var db: NoteDatabase? = null
    var dao: DAO? = null
    var notes: List<Note>? = null

    override fun onCreate() {
        db = NoteDatabase.invoke(context)
        dao = db?.getNoteDao()
        notes= dao?.getAll()
    }

    override fun onDataSetChanged() {
        val notes: List<Note>? = dao?.getAll()
    }

    override fun onDestroy() {
        if ( dao?.getAll() != null) {

        }
    }

    override fun getCount(): Int {
        return notes!!.size
    }

    override fun getViewAt(p0: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widgrt_note_item).apply {

            setTextViewText(R.id.ntTitle, notes!![p0].title)
            setTextViewText(R.id.ntContent, notes!![p0].content)
            setTextViewText(R.id.ntDate, notes!![p0].date)






        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}
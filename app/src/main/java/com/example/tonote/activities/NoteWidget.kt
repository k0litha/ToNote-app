package com.example.tonote.activities

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.tonote.R


/**
 * Implementation of App Widget functionality.
 */
class NoteWidget : AppWidgetProvider() {


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        appWidgetIds.forEach { appWidgetId ->

            // Sets up the intent that points to the StackViewService that will
            // provide the views for this collection.
            val intent = Intent(context, WidgetService::class.java)
            val rv = RemoteViews(context.packageName, R.layout.note_widget).apply {
                setRemoteAdapter(R.id.ListView, intent)

                // The empty view is displayed when the collection has no items. It should be a
                // sibling of the collection view.
                //  setEmptyView(R.id.stack_view, R.id.empty_view)
            }




            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }



}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {


}


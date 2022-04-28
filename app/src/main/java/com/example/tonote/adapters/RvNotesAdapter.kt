package com.example.tonote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tonote.R
import com.example.tonote.databinding.NoteItemLayoutBinding
import com.example.tonote.model.Note
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak


class RvNotesAdapter: androidx.recyclerview.widget.ListAdapter<Note, RvNotesAdapter.NoteViewHolder>(DiffUtilCallback()) {

   inner class NoteViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
      private val contentBinding=NoteItemLayoutBinding.bind(itemView)
      val title: MaterialTextView= contentBinding.noteItemTitle
      val content: TextView = contentBinding.noteDate
      val date: MaterialTextView=contentBinding.noteDate
      val parent: MaterialCardView=contentBinding.noteItemLayoutParent
      val markwon= Markwon.builder(itemView.context)
         .usePlugin(StrikethroughPlugin.create())
         .usePlugin(TaskListPlugin.create(itemView.context))
         .usePlugin(object : AbstractMarkwonPlugin(){
            override fun configureVisitor(builder: MarkwonVisitor.Builder) {
               super.configureVisitor(builder)
               builder.on(
                  SoftLineBreak::class.java
               ){visitor , _, -> visitor.forceNewLine()}
            }

         }).build()



   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
      return NoteViewHolder(
         LayoutInflater.from(parent.context).inflate(R.layout.note_item_layout,parent,false)
      )

   }

   override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
      getItem(position).let { note ->
         holder.apply {
            title.text=note.title
            markwon.setMarkdown(content,note.content)
            date.text=note.date
            parent.setCardBackgroundColor(note.color)

            itemView.setOnClickListener{


            }
            content.setOnClickListener{

            }
         }
      }

   }


}
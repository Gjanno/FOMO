package com.pemrogandroid.fomo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
class NoteAdapter(context: Context, uploads: List<Note>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private val mContext: Context = context
    private val mUploads: List<Note> = uploads
    private lateinit var listener:OnItemClickListener

    //Holder to display
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var textViewTitle: TextView
        lateinit var imageView: ImageView
        lateinit var userName:TextView
        lateinit var dateTimeDisplay:TextView

        init {
            textViewTitle = itemView.findViewById(R.id.text_view_title)
            imageView = itemView.findViewById(R.id.image_event)
            userName=itemView.findViewById(R.id.user_details)
            dateTimeDisplay=itemView.findViewById(R.id.showing_date_time)

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mUploads.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val noteCurrent: Note = mUploads[position]
        holder.textViewTitle.text = noteCurrent.getTitle()
        holder.userName.text=noteCurrent.getUserName()
        holder.dateTimeDisplay.text=noteCurrent.getDateAndTime()
        Picasso.get()
            .load(noteCurrent.getImageUrl())
            .fit()
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetailsActivity::class.java)
            val current = mUploads[position]

            // Pass details as extras in the Intent
            intent.putExtra("title", current.getTitle())
            intent.putExtra("description", current.getDescription())
            intent.putExtra("imageUrl", current.getImageUrl())
            intent.putExtra("locationName", current.getLocationName())
            intent.putExtra("latitude", current.getLatitude())
            intent.putExtra("longitude", current.getLongitude())
            intent.putExtra("dateAndTime", current.getDateAndTime())
            intent.putExtra("userName", current.getUserName())

            mContext.startActivity(intent)
        }
    }
}

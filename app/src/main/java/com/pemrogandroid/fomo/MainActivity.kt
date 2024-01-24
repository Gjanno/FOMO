package com.pemrogandroid.fomo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query


class MainActivity : AppCompatActivity() {


    private lateinit var mRecyclerViewEdu: RecyclerView
    private lateinit var mAdapterEdu: NoteAdapter
    private lateinit var mRecyclerViewSport: RecyclerView
    private lateinit var mAdapterSport: NoteAdapter

    private lateinit var mDatabaseRef:DatabaseReference
    private lateinit var mSportUploads:MutableList<Note>
    private lateinit var mEduUploads:MutableList<Note>
    private lateinit var bottomNavigationView: BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNav()
        setupRecycleView()
    }
    private fun setupBottomNav()
    {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ActivityMenu -> {
                    //do nothing as we are in activity menu

                    true
                }
                R.id.AddActivity -> {
                    val intent: Intent =Intent(applicationContext,NewNoteActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.ProfileMenu -> {
                    val intent: Intent = Intent(applicationContext, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                else -> false
            }
        }
    }


    private fun setupRecycleView()
    {
        mRecyclerViewSport=findViewById(R.id.recycler_view_sport)
        mRecyclerViewEdu=findViewById(R.id.recycler_view_Edu)
        mRecyclerViewSport.setHasFixedSize(true)
        mRecyclerViewEdu.setHasFixedSize(true)
        // Set up a horizontal LinearLayoutManager for Sport RecyclerView
        val layoutManagerSport = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerViewSport.layoutManager = layoutManagerSport

        // Set up a horizontal LinearLayoutManager for Edu RecyclerView
        val layoutManagerEdu = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerViewEdu.layoutManager = layoutManagerEdu

        mSportUploads = ArrayList()
        mEduUploads = ArrayList()
        mDatabaseRef=FirebaseDatabase.getInstance().getReference("Notes")
        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the existing list to avoid duplicate entries
                mSportUploads.clear()
                mEduUploads.clear()
                for(noteSnapshot in dataSnapshot.children)
                {
                    val note: Note = noteSnapshot.getValue(Note::class.java)!!

                    // Filter notes based on genre and add to the corresponding list
                    when (note.getGenre()) {
                        "Sport" -> (mSportUploads as ArrayList<Note>).add(note)
                        "Education" -> (mEduUploads as ArrayList<Note>).add(note)
                        // Add other genres if needed
                    }
                }
                // Set up adapters based on the filtered lists
                mAdapterSport = NoteAdapter(this@MainActivity, mSportUploads)
                mAdapterEdu = NoteAdapter(this@MainActivity, mEduUploads)

                mRecyclerViewSport.adapter = mAdapterSport
                mRecyclerViewEdu.adapter = mAdapterEdu

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"error",Toast.LENGTH_SHORT).show()
            }
        })

    }


}


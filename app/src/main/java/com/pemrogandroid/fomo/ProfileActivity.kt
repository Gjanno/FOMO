package com.pemrogandroid.fomo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.firebase.database.*

private lateinit var mDatabaseRef: DatabaseReference
private lateinit var bottomNavigationView: BottomNavigationView
private lateinit var mUploads: MutableList<Note>
private lateinit var mAdapter: NoteAdapter
private lateinit var mRecyclerViewProfile: RecyclerView
private lateinit var emailUser:TextView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        mRecyclerViewProfile = findViewById(R.id.recycler_view_my_profile)

        // Initialize bottom navigation, swipe-to-delete, and RecyclerView
        setupBottomNav()
        setupSwipeToDelete()
        initRecyclerView()
        // Set up ActionBar with a logout icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.symbol_logout)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the logout action
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * setup user email to be displayed
     */



    /**
     * Set up the bottom navigation menu with item selection handling.
     */
    private fun setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ActivityMenu -> {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    false
                }

                R.id.AddActivity -> {
                    // Navigate to NewNoteActivity
                    val intent = Intent(applicationContext, NewNoteActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.ProfileMenu -> {
                    // Already in the profile menu
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Initialize the RecyclerView with its adapter and set up the data observer.
     */
    private fun initRecyclerView() {
        mRecyclerViewProfile.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerViewProfile.layoutManager = layoutManager

        mUploads = ArrayList()
        mAdapter = NoteAdapter(this@ProfileActivity, mUploads)
        mRecyclerViewProfile.adapter = mAdapter

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Notes")
        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUploads.clear()

                for (noteSnapshot in dataSnapshot.children) {
                    val note: Note = noteSnapshot.getValue(Note::class.java)!!

                    // Filter notes based on userName being equal to the current email account
                    if (note.getUserName() == " " + FirebaseAuth.getInstance().currentUser?.email) {
                        mUploads.add(note)
                    }
                }

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Set up swipe-to-delete functionality using ItemTouchHelper.
     */
    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.getAbsoluteAdapterPosition()
                val noteToDelete = mUploads[position]

                // Delete the note from the database
                mDatabaseRef.child(noteToDelete.getUploadId()).removeValue()

                // Notify the adapter to update the UI
                mUploads.removeAt(position)
                mAdapter.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerViewProfile)
    }
}

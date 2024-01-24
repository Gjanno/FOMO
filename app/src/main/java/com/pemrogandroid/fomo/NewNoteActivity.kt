package com.pemrogandroid.fomo

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.opencensus.stats.View
import java.text.SimpleDateFormat
import java.util.*


class NewNoteActivity : AppCompatActivity()
{

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription:EditText
    private lateinit var eventPic:ImageView
    private lateinit var imageUri:Uri
    private lateinit var mStorageRef: StorageReference
    private lateinit var mDatabaseRef:DatabaseReference
    private lateinit var mUserDetails:String
    private lateinit var buttonSave:Button
    private  var longitude:Double=0.0
    private var latitude:Double=0.0
    private lateinit var locationSelected:EditText
    private var locationName=""
    private var selectedDate: String=""
    private var selectedTime: String=""
    private lateinit var buttonDateTime:Button
    private lateinit var displayDateTime:TextView
    private lateinit var mySpinner:Spinner
    private var selectedGenre=""
    private lateinit var bottomNavigationView: BottomNavigationView


    // Function to handle the result of the Place Autocomplete activity
    private var placeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            val place:Place=Autocomplete.getPlaceFromIntent(data!!)
            locationName= place.name!!
            latitude= place.latLng.latitude
            longitude=place.latLng.longitude
            locationSelected.setText(locationName)


        }
        else if(result.resultCode==AutocompleteActivity.RESULT_ERROR)
        {
            Toast.makeText(this,"Error Getting Location",Toast.LENGTH_SHORT).show()
        }
    }

    // Function to handle the result of the file selection activity
    private val fileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data: Intent? = result.data
                imageUri = data?.data ?: return@registerForActivityResult
                eventPic.setImageURI(imageUri)

            }
        }


    //Receive input from user
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        // Initialize UI elements

        mySpinner=findViewById(R.id.mySpinner)
        editTextTitle=findViewById(R.id.edit_text_title)
        editTextDescription=findViewById(R.id.edit_text_description)
        eventPic=findViewById(R.id.image_uploaded)
        buttonSave=findViewById(R.id.btnSaveNote)
        mStorageRef=FirebaseStorage.getInstance().getReference("Notes")
        mDatabaseRef=FirebaseDatabase.getInstance().getReference("Notes")
        locationSelected=findViewById(R.id.User_location)
        buttonDateTime=findViewById(R.id.Button_date_time)
        displayDateTime=findViewById(R.id.Show_date_time)
        supportActionBar?.hide()

        initializeGenre()

        initializePlace()

        setupBottomNav()

        buttonDateTime.setOnClickListener(){
            openTimeDate()
        }

        eventPic.setOnClickListener(){
            openFileChooser()
        }
        buttonSave.setOnClickListener{
            saveNote()
        }

    }
    // Function to set up bottom navigation
    private fun setupBottomNav()
    {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ActivityMenu -> {

                    val intent: Intent =Intent(applicationContext,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.AddActivity -> {
                    //doNothing
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
    // Function to initialize genre spinner
    private fun initializeGenre() {
        // Sample data for the Spinner
        val genres = arrayOf("Sport", "Education")

        // Create ArrayAdapter for the Spinner
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genres)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the ArrayAdapter to the Spinner
        mySpinner.adapter = arrayAdapter
        // Set OnItemSelectedListener to the Spinner
        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Get the selected genre
                selectedGenre = genres[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedGenre = "Sport"
            }
        }

    }

    // Function to handle date and time selection
    private fun openTimeDate() {
        val calendar = Calendar.getInstance()
        val year = 2024
        val month = 0 // Note: Months are zero-based, so January is 0
        val day = 5

        val dialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Handle the selected date
                // You can do something with selectedYear, selectedMonth, and selectedDayOfMonth
                val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
                val date = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                }
                selectedDate = dateFormat.format(date.time)
                openTimePicker()
            },
            year,
            month,
            day
        )

        // Show the DatePickerDialog
        dialog.show()
    }
    private fun openTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = 12
        val minute = 0

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                // Handle the selected time
                // You can do something with selectedHour and selectedMinute
                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                // Now you have both date and time in selectedDate and selectedTime
                // Do something with them as needed
                val concatenate= " $selectedDate $selectedTime"
                displayDateTime.text=concatenate
            },
            hour,
            minute,
            true // 24-hour format

        )

        // Show the TimePickerDialog
        timePickerDialog.show()

    }

    // Function to initialize Places API
    private fun initializePlace()
    {
        //initialize Places
        Places.initialize(applicationContext,"Key")
        locationSelected.isFocusable=false
        locationSelected.setOnClickListener {
            //initialize place field list
            val fieldList: List<Place.Field> = listOf(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.NAME
            )
            //create intent
            val intent:Intent=Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fieldList)
                .build(this)

            //start activity result
            placeLauncher.launch(intent)


        }



    }

    // Function to handle image file selection
    private fun openFileChooser(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        fileLauncher.launch(intent)

    }


    // Function to get user details
    private fun userDetails():String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if the user is signed in
        return if (currentUser != null) {

            val email = " "+currentUser.email

            // Display the name and email in a string
            val userInfo = email
            userInfo

        } else {
            val placeHold="PlaceHolder"
            placeHold
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                // Handle the Up button (back arrow) press
                navigateUpToParent()
                true
            }

            else-> super.onOptionsItemSelected(item)
        }
    }


    // Function to get file extension from URI
    private fun getFileExtension(uri:Uri):String
    {
        val cR: ContentResolver =contentResolver
        val mime:MimeTypeMap=MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))!!
    }

    // Function to save the note
    private fun saveNote()
    {

        val title: String=editTextTitle.text.toString()
        val description: String=editTextDescription.text.toString()
        mUserDetails=userDetails()


        if(title.trim().isEmpty()||description.trim().isEmpty())//remove all empty space
        {
            Toast.makeText(this,"Please insert title/description",Toast.LENGTH_SHORT).show()
            return
        }
        if (locationName.isEmpty())
        {
            Toast.makeText(this,"Please insert Event Location",Toast.LENGTH_SHORT).show()
            return
        }
        if( selectedDate.isEmpty()||selectedTime.isEmpty())
        {
            Toast.makeText(this,"Please insert Event Date/Time",Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("SelectedGenre", "Selected Genre: $selectedGenre")


        val fileReference:StorageReference=mStorageRef.child(System.currentTimeMillis().toString()+"."+getFileExtension(imageUri))
        fileReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL from the task snapshot
                val urlTask: Task<Uri> = taskSnapshot.storage.downloadUrl

                urlTask.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        // Save the Note object to your database
                        val uploadId = mDatabaseRef.push().key

                        // Use the download URL to create your Note object
                        val upload = Note(title, description, downloadUrl.toString(),mUserDetails,longitude,latitude,locationName,selectedDate,selectedTime,selectedGenre,
                            uploadId!!
                        )


                        if (uploadId != null) {
                            mDatabaseRef.child(uploadId).setValue(upload)
                        }

                        Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle the error
                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload Error", Toast.LENGTH_SHORT).show()
            }


        Toast.makeText(this,"New note added",Toast.LENGTH_SHORT).show()

        navigateUpToParent()
    }


    // Function to navigate up to the parent activity
    private fun navigateUpToParent() {
        // Create an Intent to navigate back to the parent activity (MainActivity)
        val intent = Intent(this, MainActivity::class.java)

        // Start the intent
        startActivity(intent)

        // Finish the current activity
        finish()
    }


}
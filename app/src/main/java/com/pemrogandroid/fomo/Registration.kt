package com.pemrogandroid.fomo

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class Registration : AppCompatActivity() {

    //Declare variables to be used for registration
    lateinit var editTextEmail:TextInputEditText
    lateinit var editTextPassword:TextInputEditText
    lateinit var editTextName:TextInputEditText
    lateinit var buttonReg:Button
    lateinit var auth: FirebaseAuth
    lateinit var progressBar:ProgressBar
    lateinit var textView:TextView

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent:Intent=Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth=FirebaseAuth.getInstance()

        //initializing variable for registration
        editTextEmail=findViewById(R.id.email)
        editTextPassword=findViewById(R.id.password)
        editTextName=findViewById(R.id.User_name)
        buttonReg=findViewById(R.id.btn_register)
        progressBar=findViewById(R.id.progressBar)
        textView=findViewById(R.id.loginNow)


        textView.setOnClickListener(){
            val intent: Intent= Intent(applicationContext,Login::class.java)
            startActivity(intent)
            finish()
        }


        buttonReg.setOnClickListener(){
            progressBar.visibility = View.VISIBLE
            var email:String = editTextEmail.getText().toString()
            var password:String = editTextPassword.getText().toString()
            var name:String=editTextName.getText().toString()

            if(TextUtils.isEmpty(email))
            {
                Toast.makeText(this,"Enter Email",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password))
            {
                Toast.makeText(this,"Enter Password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(name))
            {
                Toast.makeText(this,"Enter Name",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility=View.GONE

                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name) // Set the display name to the user's name
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                } else {
                                    Log.e(TAG, "Failed to update user profile.")
                                }
                            }

                        Toast.makeText(
                            this,
                            "Account Created",
                            Toast.LENGTH_SHORT,
                        ).show()

                        }else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }


        }
    }
}
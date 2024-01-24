package com.pemrogandroid.fomo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    //Declare variables to be used for registration
    lateinit var editTextEmail: TextInputEditText
    lateinit var editTextPassword: TextInputEditText
    lateinit var buttonLogin: Button
    lateinit var auth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    lateinit var textView: TextView

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
        setContentView(R.layout.activity_login)

        auth=FirebaseAuth.getInstance()

        //initializing variable for registration
        editTextEmail=findViewById(R.id.email)
        editTextPassword=findViewById(R.id.password)
        buttonLogin=findViewById(R.id.btn_login)
        progressBar=findViewById(R.id.progressBar)
        textView=findViewById(R.id.RegisterNow)

        textView.setOnClickListener(){
            val intent: Intent = Intent(applicationContext,Registration::class.java)
            startActivity(intent)
            finish()
        }


        buttonLogin.setOnClickListener(){
            progressBar.visibility = View.VISIBLE
            var email:String = editTextEmail.getText().toString()
            var password:String = editTextPassword.getText().toString()

            if(TextUtils.isEmpty(email))
            {
                Toast.makeText(this,"Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password))
            {
                Toast.makeText(this,"Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility=View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(applicationContext,"Login Successfull",Toast.LENGTH_SHORT).show()
                        val intent:Intent=Intent(applicationContext,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
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
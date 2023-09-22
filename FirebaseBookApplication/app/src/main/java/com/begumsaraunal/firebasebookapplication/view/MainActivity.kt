// -------------------------------------------------------------------------------------------------
// Title: MainActivity Class
// Author: Begüm Şara Ünal
// Description: This Kotlin code describes the functionality of a screen that contains
// the functionality for an Android app to create an account and log in using Firebase.
// -------------------------------------------------------------------------------------------------

//-------------------------------------------------
// Summary: Package to which the script file belongs
//-------------------------------------------------
package com.begumsaraunal.firebasebookapplication.view

//--------------------------------------------------------------------------------------------------
// Summary: Imports all required dependencies. Firebase libraries and other Android components
// required to perform Firebase related operations are imported.
//--------------------------------------------------------------------------------------------------
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.begumsaraunal.firebasebookapplication.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//--------------------------------------------------------------------------------------------------
// Summary: This class contains the functionality of the account creation screen and
// extends the AppCompatActivity class.
//--------------------------------------------------------------------------------------------------
class MainActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private  lateinit var binding :ActivityMainBinding //create binding for reach xml

    //----------------------------------------------------------------------------------------------
    // Summary:  This method is called when the activity is created.
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Initializes Firebase
        FirebaseApp.initializeApp(this)

        //Creates the data binding and places it on the screen.
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initializes the Firebase authentication (auth) instance.
        auth = Firebase.auth

        //Checks for automatic login. If a user is logged in, redirects to the UserActivity screen.
        val user = auth.currentUser
        //current user is nullable check this
        if(user != null)
        {
            //go next activity with using intent
            val intent = Intent(this,UserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //----------------------------------------------------------------------------------------------
    // Summary: This method allows the user to log in.
    //----------------------------------------------------------------------------------------------
    fun login(view: View)
    {
        //Checks the email and password fields.
        if(binding.mailText.text.toString().isNotEmpty()
            && binding.passwordText.text.toString().isNotEmpty())
        { //Attempts to log in the user with Firebase (auth.signInWithEmailAndPassword).
            auth.signInWithEmailAndPassword(binding.mailText.text.toString(),
                binding.passwordText.text.toString()).addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    // In case of successful login,
                    // it gets the user's name and displays it on the screen.
                    val user = auth.currentUser?.displayName.toString()
                    Toast.makeText(
                        applicationContext,
                        "Welcome back $user",
                        Toast.LENGTH_LONG)
                        .show()

                    //When the user logs in, it redirects to the UserActivity screen.
                    val intent = Intent(this,UserActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    it.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
        else
        {
            Toast.makeText(
                applicationContext,
                "User is not found. Please sign in.",
                Toast.LENGTH_LONG)
                .show()
        }
    }
    //----------------------------------------------------------------------------------------------
    // Summary: This method is used to create a new account.
    // When this method is called, it redirects to the CreateAccountActivity screen.
    //----------------------------------------------------------------------------------------------
    fun createAccount(view: View)
    {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
        finish()
    }
}
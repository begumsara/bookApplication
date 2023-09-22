// -------------------------------------------------------------------------------------------------
// Title: CreateAccountActivity Class
// Author: Begüm Şara Ünal
// Description: This Kotlin code is part of an Android app that enables creating an account and
// logging in using Firebase.
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
import com.begumsaraunal.firebasebookapplication.databinding.ActivityCreateAccountBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

//--------------------------------------------------------------------------------------------------
// Summary: This class contains the functionality of the account creation screen and
// extends the AppCompatActivity class.
//--------------------------------------------------------------------------------------------------
class CreateAccountActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private  lateinit var binding : ActivityCreateAccountBinding //create binding for reach xml

    // ---------------------------------------------------------------------------------------------
    // Summary: This method is called when the activity is created.
    // It initializes Firebase, creates the data binding and checks for an existing user.
    // If there is an existing user, it redirects to the UserActivity screen.
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Initializes Firebase
        FirebaseApp.initializeApp(this)

        //Creates the data binding and places it on the screen.
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initializes the Firebase authentication (auth) instance.
        auth = Firebase.auth

        //for automatic login
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

    // ---------------------------------------------------------------------------------------------
    // Summary: This method allows the user to create an account.
    // It checks the login fields such as email, password and username and
    // creates a new user account using Firebase with this information.
    // When the account is successfully created, it updates the user's profile information,
    // displays a message and redirects to the UserActivity screen.
    //----------------------------------------------------------------------------------------------
    fun register(view: View)
    {
        if(binding.mailText.text.toString().isNotEmpty() &&
            binding.passwordText.text.toString().isNotEmpty() &&
            binding.usernameText.text.toString().isNotEmpty())
        {
            auth.createUserWithEmailAndPassword(
                binding.mailText.text.toString(),
                binding.passwordText.text.toString()).addOnCompleteListener{ task ->

                if (task.isSuccessful)
                {
                    // update username
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = binding.usernameText.text.toString()
                    }

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {task ->
                        if (task.isSuccessful)
                        {
                            Toast.makeText(
                                applicationContext,
                                "User added successfully",
                                Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    //to go next activity with using intent
                    val intent = Intent(this, UserActivity::class.java)
                    //start activity
                    startActivity(intent)
                    finish() //finish activity
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
        else
        {
            Toast.makeText(
                applicationContext,
                "Email, password and username cannot be empty!",
                Toast.LENGTH_LONG)
                .show()
        }
    }
}
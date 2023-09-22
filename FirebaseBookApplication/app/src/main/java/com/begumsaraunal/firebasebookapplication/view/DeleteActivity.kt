// -------------------------------------------------------------------------------------------------
// Title: DeleteActivity Class
// Author: Begüm Şara Ünal
// Description: This Kotlin code contains the functionality of an
// Android app screen called "DeleteActivity".
// This screen allows the user to delete book information from the Firebase Firestore database.
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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.begumsaraunal.firebasebookapplication.R
import com.begumsaraunal.firebasebookapplication.databinding.ActivityDeleteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

//--------------------------------------------------------------------------------------------------
// Summary: This class contains the functionality of the screen where
// the user can delete book information.
// It also allows the user to log out and add a new book.
//--------------------------------------------------------------------------------------------------
class DeleteActivity : AppCompatActivity()
{
    private val database = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private val storage = Firebase.storage
    // Create a storage reference from our app
    private val storageRef = storage.reference

    private lateinit var binding: ActivityDeleteBinding

    //--------------------------------------------------------------------------------------------------
    // Summary: onCreateOptionsMenu and onOptionsItemSelected Methods:
    // These methods create the menu at the top of the screen and
    // determine what happens when menu items are clicked.
    // For example, selecting "Log Out" will log out,
    // or selecting "Add Book" will redirect to a new book addition screen.
    //--------------------------------------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        //create an inflater to bind the menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_bar, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_menu) //when logout item selected
        {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (item.itemId == R.id.add_book_menu) //when add book item selected
        {
            //go adding book page(activity)
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    //--------------------------------------------------------------------------------------------------
    // Summary:This method is called when the screen is created.
    //--------------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)

        //Creates the data binding and places it on the screen.
        binding = ActivityDeleteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        // Gets the book information from the Intent and displays it on the screen.
        val bookName = intent.getStringExtra("bookName")
        val bookContent = intent.getStringExtra("bookContent")
        var bookAuthor = intent.getStringExtra("bookAuthor")
        // Show values
        if (bookName != null && bookContent != null && bookAuthor != null) {
            binding.bookNameText.setText(bookName)
            binding.bookContextText.setText(bookContent)
            binding.bookAuthorText.setText(bookAuthor)
        }//Calls the delete function when the delete button is clicked.
        binding.deleteButton.setOnClickListener {
            if (bookName != null) {
                delete(bookName)
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Summary: This method is used to delete a specific book information
    // from the Firebase Firestore database.
    // It finds the matching data according to the book name and deletes this data.
    //--------------------------------------------------------------------------------------------------
    fun delete(bookName: String) {
        database.collection("BookInformations")
            .whereEqualTo("bookName", bookName).get()
            .addOnSuccessListener {
                for (document in it.documents)
                {
                    document.reference.delete()
                }
                Toast.makeText(
                    applicationContext,
                    "Book information deleted",
                    Toast.LENGTH_LONG)
                    .show()
                finish()
            }.addOnFailureListener { exception ->
            Toast.makeText(
                applicationContext,
                exception.localizedMessage,
                Toast.LENGTH_LONG)
                .show()
        }
    }
}

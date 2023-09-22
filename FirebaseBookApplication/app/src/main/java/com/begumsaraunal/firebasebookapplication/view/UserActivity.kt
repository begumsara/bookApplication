// -------------------------------------------------------------------------------------------------
// Title: UserActivity Class
// Author: Begüm Şara Ünal
// Description: This Kotlin code contains the functionality of a screen called
// "UserActivity" of an Android app. This screen provides an interface where the user can
// view book information after logging in.
// -------------------------------------------------------------------------------------------------

//-------------------------------------------------
// Summary: Package to which the script file belongs
//-------------------------------------------------
package com.begumsaraunal.firebasebookapplication.view

//--------------------------------------------------------------------------------------------------
// Summary: Imports all required dependencies. Firebase libraries and other Android components
// required to perform Firebase related operations are imported.
//--------------------------------------------------------------------------------------------------
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.begumsaraunal.firebasebookapplication.R
import com.begumsaraunal.firebasebookapplication.R.*
import com.begumsaraunal.firebasebookapplication.adapter.ItemClickListener
import com.begumsaraunal.firebasebookapplication.adapter.RecyclerAdapter
import com.begumsaraunal.firebasebookapplication.databinding.ActivityUserBinding
import com.begumsaraunal.firebasebookapplication.model.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

//--------------------------------------------------------------------------------------------------
// Summary: This class contains the functionality of the main screen where users can view
// book information after the account login.
// It also implements the ItemClickListener interface.
//--------------------------------------------------------------------------------------------------
class UserActivity : AppCompatActivity() , ItemClickListener
{

    private lateinit var auth: FirebaseAuth
    //create firestore database
    private val database = Firebase.firestore

    //create array list for book information's
    private val books = arrayListOf<Book>()
    private var bookName : String? = null
    private var username: String? = null
    private var bookContent: String? = null
    private var bookAuthor: String? = null

    private lateinit var binding: ActivityUserBinding

    //----------------------------------------------------------------------------------------------
    // Summary: This method creates the menu at the top of the screen.
    // This menu gives the user options such as logging out or adding a new book.
    //----------------------------------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        //create an inflater to bind the menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_bar,menu)

        return super.onCreateOptionsMenu(menu)
    }

    //----------------------------------------------------------------------------------------------
    // Summary: This method determines what happens when the user selects menu items.
    // For example, logging out when "Log Out" is selected or
    // redirecting to a new book screen when "Add Book" is selected.
    //----------------------------------------------------------------------------------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == id.logout_menu) //when logout item selected
        {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        else if (item.itemId == id.add_book_menu) //when add book item selected
        {
            //go adding book page(activity)
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
    //----------------------------------------------------------------------------------------------
    // Summary: This method is called when the screen is created.
    //----------------------------------------------------------------------------------------------
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_user)
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Creates the data binding and places it on the screen.
        binding = ActivityUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Creates a toolbar and adds it to the screen.
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Firebase calls getDataFromDatabase to retrieve book information from the Firestore database.
        getDataFromDatabase()

        //Sets how to display the RecyclerView and sets the data context.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RecyclerAdapter(books,this)
    }

    //----------------------------------------------------------------------------------------------
    // Summary: This special method pulls book information from the Firebase Firestore database and
    // creates a list for the RecyclerView.
    //----------------------------------------------------------------------------------------------
    @SuppressLint("NotifyDataSetChanged")
    private fun getDataFromDatabase()
    {
        // It uses addSnapshotListener to fetch book information from the database.
        //descending order of date
        database.collection("BookInformations")
            .orderBy("date",Query.Direction.DESCENDING)
            .addSnapshotListener {snapshot, error ->

            if (error != null)
            {
                //If an error occurs, it displays an error message to the user.
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }
            else
            {
                //If the data is retrieved successfully, Firebase loops the Firestore documents and
                // creates Book objects using this information.
                if(snapshot != null && snapshot.isEmpty.not())
                {
                    // It adds the book information to a list, the books list

                        val documents = snapshot.documents

                    //clear list for the duplicate same values
                        books.clear()
                        for (document in documents)
                        {
                            username = document["username"] as String?
                            bookName = document["bookName"] as String?
                            bookAuthor = document["bookAuthor"] as String?
                            bookContent = document["bookContent"] as String?
                            val bookPhotoUrl = document["bookPhotoUrl"] as String?

                            val bookInfo = Book(username,bookName,bookAuthor,bookContent,bookPhotoUrl)
                            //add array list
                            books.add(bookInfo)
                        }// notifies the RecyclerView of data changes
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    // Summary: This method determines what happens when items in the RecyclerView are clicked.
    // It provides functionality where the user can delete their own books or not see someone else's books.
    //----------------------------------------------------------------------------------------------
    override fun onItemClick(position: Int)
    {
        if(books[position].username?.equals(auth.currentUser!!.displayName.toString()) == true)
        {
            val intent = Intent(this, DeleteActivity::class.java)
            intent.putExtra("bookName",books[position].bookName)
            intent.putExtra("bookAuthor",books[position].bookAuthor)
            intent.putExtra("bookContent",books[position].bookContent)
            intent.putExtra("bookPhotoUrl",books[position].bookPhotoUrl)
            startActivity(intent)
    }
        else
        {
            Toast.makeText(
                applicationContext,
                "Sorry :(( this book informations is not yours.",
                Toast.LENGTH_LONG)
                .show()
        }
        }
}

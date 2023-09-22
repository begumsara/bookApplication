// -------------------------------------------------------------------------------------------------
// Title: AddBookActivity Class
// Author: Begüm Şara Ünal
// Description: This Kotlin code contains the functionality of an
// Android app's screen called "AddBookActivity".
// This screen provides an interface where the user can add book information after logging in.
// -------------------------------------------------------------------------------------------------

//-------------------------------------------------
// Summary: Package to which the script file belongs
//-------------------------------------------------
package com.begumsaraunal.firebasebookapplication.view

//--------------------------------------------------------------------------------------------------
// Summary: Imports all required dependencies. Firebase libraries and other Android components
// required to perform Firebase related operations are imported.
//--------------------------------------------------------------------------------------------------
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.begumsaraunal.firebasebookapplication.R
import com.begumsaraunal.firebasebookapplication.databinding.ActivityAddBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

//--------------------------------------------------------------------------------------------------
// Summary: This class contains the functionality of the screen where the user can
// add new book information.
// It also contains functionality such as text limitations, image selection and
// saving data to the Firebase database.
//--------------------------------------------------------------------------------------------------
@Suppress("DEPRECATION")
class AddBookActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityAddBookBinding

    //create firabase database
    private val database = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val storage = Firebase.storage

    private lateinit var editText: EditText
    private val maxWordCount = 20

    private var chosenPhoto: Uri? = null
    private var chosenBitmap: Bitmap? = null

    //----------------------------------------------------------------------------------------------
    // Summary: This method is called when the screen is created
    //----------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //Creates the data binding and places it on the screen.
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Initializes the Firebase authentication (auth) instance.
        auth = Firebase.auth

        //Creates a toolbar and activates the return arrow.
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            onBackPressed()
        }

        //Adds a TextWatcher to check for text limitations.
        editText = binding.contentText
        editText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?)
            {//after text change
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {//before text change
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
                val words = p0?.trim()?.split("\\s+".toRegex()) ?: emptyList()
                if(words.size > maxWordCount) {
                    showAlertMessage()
                }
            }
        })
    }
    //----------------------------------------------------------------------------------------------
    // Summary: This method displays an alert dialog to the user.
    // This warning is displayed when text input exceeds the limitations.
    //----------------------------------------------------------------------------------------------
    fun showAlertMessage()
    {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Mistake")
            .setMessage("Text count must be lower than 100 word")
            .setPositiveButton("I will try again.") { dialog, _ ->
                dialog.dismiss()
            }.create()
        alertDialog.show()
    }
    //connect xml and our code
    // the part where we will connect the menu with resource
    //----------------------------------------------------------------------------------------------
    // Summary: onCreateOptionsMenu and onOptionsItemSelected Methods:
    // These methods create the menu at the top of the screen and
    // determine what happens when menu items are clicked.
    // For example, selecting "Log Out" logs you out of the session,
    // or selecting "Add Book" takes you to the screen for adding a new book.
    //----------------------------------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        //create an inflater to bind the menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_bar,menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == R.id.logout_menu) //when logout item selected
        {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        else if (item.itemId == R.id.add_book_menu) //when add book item selected
        {
            //go adding book page(activity)
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
    //----------------------------------------------------------------------------------------------
    // Summary: save and addImage Methods:
    // These methods manage saving book information and adding a book image respectively.
    // The book image is selected from the gallery and uploaded to Firebase Storage.
    //----------------------------------------------------------------------------------------------
    fun save(view: View)
    {
        val reference = storage.reference
        val uuid = UUID.randomUUID()
        val photoName = "${uuid}.jpg"
        val photoRef = reference.child("photos").child(photoName)
        if(chosenPhoto != null)
        {
            photoRef.putFile(chosenPhoto!!)
                .addOnSuccessListener {
                    // URL al
                    val downloadPhotoRef = reference.child("photos").child(photoName)
                    downloadPhotoRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        saveToDatabase(downloadUrl)
                    }.addOnFailureListener { exception ->
                        // URL alırken bir hata oluştuğunda burası çalışır
                        Toast.makeText(
                            applicationContext,
                            exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Yükleme işlemi başarısız olduğunda burası çalışır
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
        }
        else
        {
            saveToDatabase(null)
        }
    }
    fun addImage(view: View)
    {
        //izni ve android sürümünü kontrol et(androidin eski sürümlerinde izinsin erişilebiliyordu)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin verilmemiş izni iste
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
        else
        {
            //izin verilmiş direkt galeriye gidebiliriz
            val photosIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //eski android sürümleri için
            startActivityForResult(photosIntent, 2)
        }
    }

    //----------------------------------------------------------------------------------------------
    // Summary: This method saves the book information to the Firebase Firestore database.
    // The data is stored using a HashMap and added to the database.
    //----------------------------------------------------------------------------------------------
    private fun saveToDatabase(downloadUrl: String?)
    {
//create hash map to store data
        val bookMap = HashMap<String, Any>()
        bookMap["username"] = auth.currentUser!!.displayName.toString()
        bookMap["bookName"] = binding.titleText.text.toString()
        bookMap["bookAuthor"] = binding.authorText.text.toString()
        bookMap["bookContent"] = binding.contentText.text.toString()
        bookMap["date"] = com.google.firebase.Timestamp.now()
        if (downloadUrl != null)
        {
            bookMap["bookPhotoUrl"] = downloadUrl
        }
        database.collection("BookInformations")
            .add(bookMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    //----------------------------------------------------------------------------------------------
    // Summary: onRequestPermissionsResult and upload Methods:
    // These methods check if the user has granted permissions and
    // if permissions are granted, they provide access to the gallery.
    //----------------------------------------------------------------------------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        if (requestCode == 1 && upload(grantResults))
        {
                //izin verilmiş direkt galeriye gidebiliriz
                val photosIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(photosIntent, 2)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun upload(grantResults: IntArray):Boolean
    {
       return  grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    //----------------------------------------------------------------------------------------------
    // Summary: This method processes the book image selected from the gallery and
    // displays the image on the screen.
    //----------------------------------------------------------------------------------------------
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null)
        {
            chosenPhoto = data.data
            if (chosenPhoto != null)
            {
                val source = ImageDecoder.createSource(this.contentResolver, chosenPhoto!!)
                chosenBitmap = ImageDecoder.decodeBitmap(source)
                binding.imageView.setImageBitmap(chosenBitmap)
            }
            else
            {
                binding.imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
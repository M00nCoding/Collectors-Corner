package com.example.CollectorsCorner


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.Calendar
import java.util.UUID

class AddBookActivity: AppCompatActivity() {

    //Declarations
    private lateinit var ivBookImage : ImageView
    private lateinit var btnCamera : Button
    private lateinit var etBookTitle : EditText
    private lateinit var etBookDescription : EditText
    private lateinit var tvBookDateAcquisition : TextView
    private lateinit var btnBookDateAcquisition : Button
    private lateinit var spinnerBookGenre : Spinner
    private lateinit var btnAddBook : Button

    private lateinit var imageBitmap: Bitmap
    private lateinit var imageUrl : String

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addbook)

        //Initialize variables
        ivBookImage = findViewById(R.id.bookImageIV)
        btnCamera = findViewById(R.id.cameraBtn)
        etBookTitle = findViewById(R.id.bookTitleEt)
        etBookDescription = findViewById(R.id.bookDescriptionEt)
        tvBookDateAcquisition = findViewById(R.id.bookDateAcquisitionTv)
        btnBookDateAcquisition = findViewById(R.id.bookDateAcquisitionBtn)
        spinnerBookGenre = findViewById(R.id.bookGenreSpinner)
        btnAddBook = findViewById(R.id.addBookBtn)

        //Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("Book")

        //Loads the captured photo into the Image View
        val getResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    val bitmap = it.data!!.extras?.get("data") as Bitmap
                    ivBookImage.setImageBitmap(bitmap)
                    imageBitmap = bitmap
                }
            }

        //Opens the camera
        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(intent)
        }

        //Pick date and display it in Text View
        btnBookDateAcquisition.setOnClickListener {
            val c = Calendar.getInstance()

            val yearC = c.get(Calendar.YEAR)
            val monthC = c.get(Calendar.MONTH)
            val dayC = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    tvBookDateAcquisition.text =
                        (year.toString() + "/" + (month + 1).toString() + "/" + day.toString())
                },
                yearC,
                monthC,
                dayC
            )
            datePickerDialog.show()
        }

        //Set on click listener to the Add Book button
        btnAddBook.setOnClickListener {
            saveBookData()
        }


        // Set OnClickListener for the "Library" button to navigate back
        findViewById<Button>(R.id.library_btn3).setOnClickListener {
            // Create intent to navigate back to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            // Finish this activity
            finish()
        }

        // Set OnClickListener for the "Add Genre" button to navigate to AddGenreActivity
        findViewById<Button>(R.id.add_genre_btn).setOnClickListener {
            val intent = Intent(this, AddGenreActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for navigating to the achievements page
        findViewById<Button>(R.id.achievements_button).setOnClickListener {
            // Create intent to navigate to AchievementsActivity
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun uploadBitmapToFirebase(bitmap: Bitmap, imageName: String) {

        // Create a reference to the Firebase Storage location
        val storageReference = Firebase.storage.reference.child("images/$imageName.jpg")

        // Convert bitmap to byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val data = outputStream.toByteArray()

        // Upload image to Firebase Storage
        storageReference.putBytes(data)

        //Get image URL
        imageUrl = storageReference.downloadUrl.toString()

    }

    //Save book data function
    private fun saveBookData(){

        //Get values from fields
        val bookTitle = etBookTitle.text.toString()
        val bookDescription = etBookDescription.text.toString()
        val bookDateAcquisition = tvBookDateAcquisition.text.toString()

        uploadBitmapToFirebase(imageBitmap, bookTitle)

        //Auth
        auth = FirebaseAuth.getInstance()

        //Image URL
        val imageUrl = imageUrl

        //Creates a unique Book Id
        val bookId = dbRef.push().key!!

        //Gets the User Id
        val uid = auth.currentUser?.uid.toString()

        //Insert values into the BookModel class
        val book = BookModel(bookId, bookTitle, bookDescription, bookDateAcquisition, imageUrl, uid)

        //Insert book data into the firebase real-time database
        dbRef.child(bookId).setValue(book)
            .addOnCompleteListener{
                //Display message if inserting the book data was successful
                Toast.makeText(this, "Book added successfully!", Toast.LENGTH_LONG).show()

                //Clear fields
                etBookTitle.text.clear()
                etBookDescription.text.clear()
                tvBookDateAcquisition.setText("")

            }.addOnFailureListener{ err ->
                //Display message if inserting the book data was not successful
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }

    }

}


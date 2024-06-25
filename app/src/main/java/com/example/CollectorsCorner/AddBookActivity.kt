package com.example.CollectorsCorner


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.widget.ArrayAdapter
//import android.net.Uri not used
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
//import com.google.firebase.storage.FirebaseStorage not used
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
//import java.net.URL not used
import java.util.Calendar
//import java.util.UUID not used

class AddBookActivity: AppCompatActivity() {

    //Declarations
    private lateinit var ivBookImage: ImageView
    private lateinit var btnCamera: Button
    private lateinit var etBookTitle: EditText
    private lateinit var etBookDescription: EditText
    private lateinit var tvBookDateAcquisition: TextView
    private lateinit var btnBookDateAcquisition: Button
    private lateinit var spinnerBookGenre: Spinner
    private lateinit var btnAddBook: Button

    private lateinit var imageBitmap: Bitmap
    //private lateinit var imageUrl: String

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth


    // Predefined list of genres
    private val genres = listOf( "Fiction","Fantasy", "Science Fiction", "Mystery", "Romance", "Thriller", "Horror", "Non-fiction", "Biography", "History", "Self-help")


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


        // Populate spinner with predefined genres
        val genresWithPrompt = mutableListOf("Select Genre")
        genresWithPrompt.addAll(genres)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            genresWithPrompt
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBookGenre.adapter = adapter
        spinnerBookGenre.setSelection(0)



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
            if (validateInputs()) {
                saveBookData()
            }
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

// function for user validation checks
    private fun validateInputs(): Boolean {
        val bookTitle = etBookTitle.text.toString().trim()
        val bookDescription = etBookDescription.text.toString().trim()
        val bookDateAcquisition = tvBookDateAcquisition.text.toString().trim()
        val selectedGenre = spinnerBookGenre.selectedItem.toString()

        if (bookTitle.isEmpty()) {
            etBookTitle.error = "Enter Book Title"
            return false
        }

        if (bookDescription.isEmpty()) {
            etBookDescription.error = "Enter Description"
            return false
        }

        if (bookDateAcquisition.isEmpty()) {
            Toast.makeText(this, "Select Date of Acquisition", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedGenre == "Select Genre") {
            Toast.makeText(this, "Select Genre", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!::imageBitmap.isInitialized) {
            Toast.makeText(this, "Please take a photo of your book cover", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    //Save book data function
    private fun saveBookData() {

        // Get values from fields
        val bookTitle = etBookTitle.text.toString()
        val bookDescription = etBookDescription.text.toString()
        val bookDateAcquisition = tvBookDateAcquisition.text.toString()
        val selectedGenre = spinnerBookGenre.selectedItem.toString()

        // Upload the image to Firebase Storage and get the download URL
        uploadBitmapToFirebase(imageBitmap, bookTitle) { imageUrl ->
            // Create a unique Book Id
            val bookId = dbRef.push().key!!

            //Auth
            auth = FirebaseAuth.getInstance()


            // Gets the User Id
            val uid = auth.currentUser?.uid.toString()


            // Insert values into the BookModel class
            val book =
                BookModel(bookId, bookTitle, bookDescription, bookDateAcquisition, imageUrl, selectedGenre, uid)


            // Insert book data into the firebase real-time database
            dbRef.child(bookId).setValue(book)
                .addOnCompleteListener {
                    // Display message if inserting the book data was successful
                    Toast.makeText(this, "Book added successfully!", Toast.LENGTH_LONG).show()


                    // Clear fields
                    etBookTitle.text.clear()
                    etBookDescription.text.clear()
                    tvBookDateAcquisition.text = "" // Clear the date acquisition text
                    spinnerBookGenre.setSelection(0) // Reset spinner selection
                    ivBookImage.setImageResource(R.drawable.cover) // Reset image to default
                }.addOnFailureListener { err ->
                    // Display message if inserting the book data was not successful
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Upload bitmap to Firebase Storage and get download URL
    private fun uploadBitmapToFirebase(
        bitmap: Bitmap,
        imageName: String,
        callback: (String) -> Unit
    ) {
        // Create a reference to the Firebase Storage location
        val storageReference = Firebase.storage.reference.child("images/$imageName.jpg")

        // Convert bitmap to byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val data = outputStream.toByteArray()

        // Upload image to Firebase Storage
        storageReference.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL for the image
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString() // Store the download URL in a variable
                    callback(imageUrl) // Pass the download URL to the callback function
                }.addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(
                        this@AddBookActivity,
                        "Failed to get download URL: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(
                    this@AddBookActivity,
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}


package com.example.CollectorsCorner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.*
import android.widget.ScrollView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import android.graphics.Color
import android.content.Context
import com.bumptech.glide.Glide


class HomeActivity : AppCompatActivity() {

    // declaring the cloud storage from firebase
    private val storage = FirebaseStorage.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var totalBooksCollected: TextView
    private lateinit var totalGenres: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var linearLayout: LinearLayout


    // Flag variable to indicate whether data is being refreshed
    private var isRefreshing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        totalBooksCollected = findViewById(R.id.books_collected)
        totalGenres = findViewById(R.id.total_categories)
        scrollView = findViewById(R.id.scrollView)
        linearLayout = findViewById(R.id.linearLayout)


        // Check if the user has logged in previously
        val isLoggedIn = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)

        // Display a success message when user has been successfully logged in for the first time
        if (!isLoggedIn) {
            Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
            // Save the login status to SharedPreferences
            getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
                .putBoolean("isLoggedIn", true).apply()
        }

        // Check if the user is logged in
        if (auth.currentUser == null) {
            // If not logged in, navigate to the LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish the current activity
            return
        }


        // Initialize the logout button
        val logoutButton = findViewById<ImageView>(R.id.logout_button)
        logoutButton.setOnClickListener {
            // Calls the logout function
            logoutUser()
        }


        // Set OnClickListener for navigating to the add genre page
        findViewById<Button>(R.id.add_genre_btn).setOnClickListener {
            // Create intent to navigate to AddGenreActivity
            val intent = Intent(this, AddGenreActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for navigating to the add book page
        findViewById<Button>(R.id.add_book_btn).setOnClickListener {
            // Create intent to navigate to AddBookActivity
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }


        // Set OnClickListener for navigating to the achievements page
        findViewById<Button>(R.id.achievements_button).setOnClickListener {
            // Create intent to navigate to AchievementsActivity
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }



        // Refresh button to help the user to refresh their list of books in the library layout
        val refreshButton = findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            // Check if data is currently being refreshed
            if (!isRefreshing) {
                // Set the flag to indicate that data is being refreshed
                isRefreshing = true

                // Call the functions to reload the data
                loadTotalBooksCollected()
                loadTotalGenres()
                loadBooks()

            } else {
                // Data is already being refreshed, ignore the click
                Toast.makeText(this, "Data is already being refreshed", Toast.LENGTH_SHORT).show()
            }
        }


        // calling the functions
        loadTotalBooksCollected()
        loadTotalGenres()
        loadBooks()


    }


    // Function to logout the user
    private fun logoutUser() {
        auth.signOut() // Sign out the current user
        // Navigate back to the LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Finish the current activity
    }


    // Function to be called after data is refreshed to reset the flag
    private fun onDataRefreshed() {
        // Reset the flag to indicate that data refresh is complete
        isRefreshing = false
    }


    // function that loads the total number of books the user collected
    private fun loadTotalBooksCollected() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("Book")
                .orderByChild("uid")
                .equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        totalBooksCollected.text = getString(R.string.total_books_collected, snapshot.childrenCount)
                        // Call onDataRefreshed after data is loaded
                        onDataRefreshed()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@HomeActivity,
                            "Failed to load total books collected.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Call onDataRefreshed even if loading fails to reset the flag
                        onDataRefreshed()
                    }
                })
        }
    }



    // function for loading the total number of genres the user has
    private fun loadTotalGenres() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("Genre")
                .orderByChild("uid")
                .equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        totalGenres.text = getString(R.string.total_genres, snapshot.childrenCount)
                        // Call onDataRefreshed after data is loaded
                        onDataRefreshed()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@HomeActivity,
                            "Failed to load total genres.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Call onDataRefreshed even if loading fails to reset the flag
                        onDataRefreshed()
                    }
                })
        }
    }

    // function to load books to the scroll view in the library layout page
    private fun loadBooks() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            linearLayout.removeAllViews() // Clear the existing list of books first before the user clicks on the refresh button
            database.child("Book")
                .orderByChild("uid")
                .equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (bookSnapshot in snapshot.children) {
                            val book = bookSnapshot.getValue(BookModel::class.java)
                            if (book != null) {
                                addBookToScrollView(book)
                            }
                        }
                        // Call onDataRefreshed after data is loaded
                        onDataRefreshed()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@HomeActivity,
                            "Failed to load books.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Call onDataRefreshed even if loading fails to reset the flag
                        onDataRefreshed()
                    }
                })
        }
    }


    // function for adding the books to the scroll view so that the user can see their
    // collected books
    private fun addBookToScrollView(book: BookModel) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 16, 0, 16)

        val cardView = CardView(this)
        cardView.layoutParams = layoutParams
        cardView.radius = 16F
        cardView.cardElevation = 8F
        cardView.setContentPadding(16, 16, 16, 16)

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        val imageView = ImageView(this)
        val params = LinearLayout.LayoutParams(200, 200)
        imageView.layoutParams = params
        imageView.setPadding(0, 0, 16, 0)

        // Load image from Firebase Storage
        if (!book.imageUrl.isNullOrEmpty()) {
            Glide.with(this@HomeActivity)
                .load(book.imageUrl)
                .placeholder(R.drawable.default_book_cover_1)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.default_book_cover_1)
        }



        linearLayout.addView(imageView)

        val bookInfoLayout = LinearLayout(this)
        bookInfoLayout.orientation = LinearLayout.VERTICAL

        val bookTitle = TextView(this)
        bookTitle.text = book.bookTitle
        bookTitle.textSize = 18F
        bookTitle.setTextColor(Color.BLACK)
        bookInfoLayout.addView(bookTitle)

        val bookDescription = TextView(this)
        bookDescription.text = book.bookDescription
        bookDescription.textSize = 14F
        bookDescription.setTextColor(Color.GRAY)
        bookInfoLayout.addView(bookDescription)

        linearLayout.addView(bookInfoLayout)

        cardView.addView(linearLayout)
        this.linearLayout.addView(cardView)
    }





}
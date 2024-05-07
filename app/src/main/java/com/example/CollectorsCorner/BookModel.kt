package com.example.CollectorsCorner

data class BookModel(
    var bookId : String? = null,
    var bookTitle : String? = null,
    var bookDescription : String? = null,
    var bookDateAcquisition : String? = null,
    var imageUrl : String? = null,
    var genre: String? = null,  // Newly added genre field
    var uid: String? = null
)
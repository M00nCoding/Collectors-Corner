package com.example.CollectorsCorner

import android.net.Uri

data class BookModel(
    var bookId : String? = null,
    var bookTitle : String? = null,
    var bookDescription : String? = null,
    var bookDateAcquisition : String? = null,
    var imageUrl : String? = null,
    var uid: String? = null
)
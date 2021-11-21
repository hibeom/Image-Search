package com.pinkcloud.imagesearch.data

data class Image(
    val id: String,
    val collection: String?,
    val thumbnailUrl: String?,
    val width: Int?,
    val height: Int?,
    val datetime: String?
)

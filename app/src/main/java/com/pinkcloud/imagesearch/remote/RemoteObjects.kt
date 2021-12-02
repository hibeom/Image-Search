package com.pinkcloud.imagesearch.remote

import com.google.gson.annotations.SerializedName
import com.pinkcloud.imagesearch.data.Image

data class ImageResponse(
    val meta: Meta,
    val documents: List<Document>
)

data class Meta(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)

data class Document(
    val collection: String?,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    val width: Int?,
    val height: Int?,
    val datetime: String?
)

fun Document.asImage(id: String): Image {
    return Image(
        id = id,
        collection = collection,
        thumbnailUrl = thumbnailUrl,
        datetime = datetime
    )
}
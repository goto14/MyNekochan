package org.miura.mynekochan.data.model

import android.graphics.Bitmap

open class ImageData {
    var id: String = ""
    var image: Bitmap? = null
    var flag: Boolean = false
    var deleted: Boolean = false
}